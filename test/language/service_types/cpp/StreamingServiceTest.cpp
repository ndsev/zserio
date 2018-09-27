#include <vector>
#include <stdexcept>
#include <memory>
#include <grpcpp/grpcpp.h>

#include "gtest/gtest.h"

#include "service_types/streaming_service/UserDB.h"

namespace service_types
{
namespace streaming_service
{

class Client
{
public:
    explicit Client(const std::shared_ptr<grpc::Channel>& channel)
    :   m_stub(UserDB::NewStub(channel))
    {}

    typedef std::pair<std::string, uint8_t> UserPair;
    typedef std::vector<UserPair> Users;

    uint32_t addUser(std::string name, uint8_t age)
    {
        User user;
        user.setName(name);
        user.setAge(age);
        Num num;
        grpc::ClientContext context;

        grpc::Status status = m_stub->addUser(&context, user, &num);
        if (status.ok())
            return num.getNum();
        else
            throw std::runtime_error("Client.addUser failed!");
    }

    uint32_t addUsers(const Users& users)
    {
        User user;
        Num num;
        grpc::ClientContext context;

        UserDB::Stub::ClientaddUsersWriterPtr writer = m_stub->addUsers(&context, &num);
        for (const auto& userPair : users)
        {
            user.setName(userPair.first);
            user.setAge(userPair.second);
            if (!writer->Write(user))
                throw std::runtime_error("Client.addUsers failed - broken stream!");
        }
        writer->WritesDone();
        grpc::Status status = writer->Finish();
        if (status.ok())
            return num.getNum();
        else
            throw std::runtime_error("Client.addUsers failed - status not ok!");
    }

    void getUsers(Users& users)
    {
        Empty empty;
        User user;
        grpc::ClientContext context;

        UserDB::Stub::ClientgetUsersReaderPtr reader = m_stub->getUsers(&context, empty);
        while (reader->Read(&user))
        {
            users.emplace_back(user.getName(), user.getAge());
        }
        grpc::Status status = reader->Finish();
        if (!status.ok())
            throw std::runtime_error("Client.getUsers failed!");
    }

    void getAges(Users& users)
    {
        grpc::ClientContext context;

        UserDB::Stub::ClientgetAgesReaderWriterPtr stream = m_stub->getAges(&context);
        Name name;
        Age age;
        for (auto& userPair : users)
        {
            name.setName(userPair.first);
            stream->Write(name);
            if (!stream->Read(&age))
                throw std::runtime_error("Client.getAges failed - reading failed!");
            userPair.second = age.getAge();
        }
        stream->WritesDone();
        grpc::Status status = stream->Finish();
        if (!status.ok())
            throw std::runtime_error("Client.getAges failed - status not ok!");
    }

    std::unique_ptr<UserDB::Stub> m_stub;
};

class Service final : public UserDB::Service
{
public:
    ::grpc::Status addUser(::grpc::ServerContext*, const service_types::streaming_service::User* request,
            service_types::streaming_service::Num* response) override
    {
        m_users[request->getName()] = *request;
        response->setNum(static_cast<uint32_t>(m_users.size()));
        return ::grpc::Status::OK;
    }

    ::grpc::Status addUsers(::grpc::ServerContext*,
            ::grpc::ServerReader<service_types::streaming_service::User>* reader,
            service_types::streaming_service::Num* response) override
    {
        User user;
        while (reader->Read(&user))
        {
            m_users[user.getName()] = user;
        }
        response->setNum(static_cast<uint32_t>(m_users.size()));
        return ::grpc::Status::OK;
    }

    ::grpc::Status getUsers(::grpc::ServerContext*,
            const service_types::streaming_service::Empty*,
            ::grpc::ServerWriter<service_types::streaming_service::User>* writer) override
    {
        for (auto it = m_users.begin(); it != m_users.end(); ++it)
        {
            writer->Write(it->second);
        }
        return ::grpc::Status::OK;
    }

    ::grpc::Status getAges(::grpc::ServerContext*, ::grpc::ServerReaderWriter<
            service_types::streaming_service::Age, service_types::streaming_service::Name>* stream)
    {
        Name name;
        Age age;
        while (stream->Read(&name))
        {
            age.setAge(m_users[name.getName()].getAge());
            stream->Write(age);
        }
        return ::grpc::Status::OK;
    }

private:
    std::map<std::string, User> m_users;
};

class StreamingServiceTest : public ::testing::Test
{
public:
    StreamingServiceTest()
    :   server(buildServer()),
        client(server->InProcessChannel(grpc::ChannelArguments()))
    {}

private:
    std::unique_ptr<grpc::Server> buildServer()
    {
        grpc::ServerBuilder serverBuilder;
        serverBuilder.RegisterService(&service);
        return serverBuilder.BuildAndStart();
    }

    Service service;
    std::unique_ptr<grpc::Server> server;

protected:
    Client client;
};

TEST_F(StreamingServiceTest, userDatabase)
{
    // no streaming
    ASSERT_EQ(1, client.addUser("A", 10));

    // client streaming
    Client::Users usersToAdd;
    usersToAdd.emplace_back("B", 15);
    usersToAdd.emplace_back("C", 20);
    ASSERT_EQ(3, client.addUsers(usersToAdd));

    // no streaming
    ASSERT_EQ(4, client.addUser("D", 25));

    // server streaming
    Client::Users allUsers;
    client.getUsers(allUsers);
    ASSERT_EQ(4, allUsers.size());

    // bidi stremaing
    Client::Users agesQuery;
    agesQuery.emplace_back("C", 0);
    agesQuery.emplace_back("B", 0);
    client.getAges(agesQuery);
    ASSERT_EQ(20, agesQuery[0].second); // C age
    ASSERT_EQ(15, agesQuery[1].second); // B age
}

} // namespace streaming_service
} // namespace service_types

