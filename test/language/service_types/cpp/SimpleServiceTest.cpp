#include <memory>
#include <grpcpp/grpcpp.h>

#include "gtest/gtest.h"

#include "zserio/BitStreamWriter.h"
#include "zserio/BitStreamReader.h"
#include "zserio/CppRuntimeException.h"

#include "service_types/simple_service/SimpleService.h"

namespace service_types
{
namespace simple_service
{

class Client
{
public:
    explicit Client(const std::shared_ptr<grpc::Channel>& channel)
    :   m_stub(SimpleService::NewStub(channel))
    {}

    uint64_t powerOfTwo(int32_t value)
    {
        Request request;
        request.setValue(value);

        Response response;

        grpc::ClientContext context;
        grpc::Status status = m_stub->powerOfTwo(&context, request, &response);

        if (status.ok())
            return response.getValue();
        else
        {
            std::cerr << status.error_message() << std::endl;
            return 0;
        }
    }

    uint64_t powerOfTwoAsync(int32_t value)
    {
        Request request;
        request.setValue(value);

        Response response;

        grpc::ClientContext context;
        grpc::CompletionQueue cq;
        grpc::Status status;
        std::unique_ptr<grpc::ClientAsyncResponseReader<Response> > rpc(
                m_stub->PrepareAsyncpowerOfTwo(&context, request, &cq));

        rpc->StartCall();
        rpc->Finish(&response, &status, (void*)1);
        bool ok = false;
        void* tag;
        cq.Next(&tag, &ok);

        if (ok && tag == (void*)1 && status.ok())
            return response.getValue();
        else
        {
            std::cerr << status.error_message() << std::endl;
            return 0;
        }
    }

    std::unique_ptr<SimpleService::Stub> m_stub;
};

class Service final : public SimpleService::Service
{
public:
    ::grpc::Status powerOfTwo(grpc::ServerContext*, const Request* request, Response* response)
    {
        int32_t value = request->getValue();
        if (value < 0)
            value = -value;
        response->setValue(static_cast<uint64_t>(value) * value);

        return grpc::Status::OK;
    }
};

class SimpleServiceTest : public ::testing::Test
{
public:
    SimpleServiceTest()
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

TEST_F(SimpleServiceTest, powerOfTwo)
{
    ASSERT_EQ(169, client.powerOfTwo(13));
    ASSERT_EQ(169, client.powerOfTwo(-13));
    ASSERT_EQ(4, client.powerOfTwo(2));
    ASSERT_EQ(4, client.powerOfTwo(-2));
}

TEST_F(SimpleServiceTest, powerOfTwoAsync)
{
    ASSERT_EQ(169, client.powerOfTwoAsync(13));
    ASSERT_EQ(169, client.powerOfTwoAsync(-13));
    ASSERT_EQ(4, client.powerOfTwoAsync(2));
    ASSERT_EQ(4, client.powerOfTwoAsync(-2));
}

} // namespace simple_service
} // namespace service_types
