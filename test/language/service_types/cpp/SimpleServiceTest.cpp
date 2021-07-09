#include <fstream>
#include <memory>

#include "gtest/gtest.h"

#include "zserio/ServiceException.h"
#include "service_types/simple_service/SimpleService.h"

using namespace zserio::literals;

namespace service_types
{
namespace simple_service
{

using allocator_type = SimpleService::Client::allocator_type;

namespace
{
    struct FakeContext
    {
        bool seenByService = false;
    };
}

class SimpleServiceImpl : public SimpleService::Service
{
public:
    Response powerOfTwoImpl(const Request& request, void* context) override
    {
        if (context != nullptr)
            static_cast<FakeContext*>(context)->seenByService = true;

        int32_t value = request.getValue();
        if (value < 0)
            value = -value;
        return Response(static_cast<uint64_t>(value) * static_cast<uint64_t>(value), get_allocator_ref());
    }
};

class SimpleServiceTest : public ::testing::Test
{
public:
    SimpleServiceTest()
    :   client(service),
        movedService(createService()),
        movedClient(createClient())
    {}

private:
    // check that move constructors work correctly
    SimpleServiceImpl createService()
    {
        // cannot be const because we need to move-construct it
        SimpleServiceImpl createdService;
        return createdService;
    }

    SimpleService::Client createClient()
    {
        // cannot be const because we need to move-construct it
        SimpleService::Client createdClient(movedService);
        return createdClient;
    }

protected:
    SimpleServiceImpl service;
    SimpleService::Client client;
    SimpleServiceImpl movedService;
    SimpleService::Client movedClient;
};

TEST_F(SimpleServiceTest, serviceFullName)
{
    ASSERT_EQ("service_types.simple_service.SimpleService"_sv,
            SimpleService::Service::serviceFullName());
}

TEST_F(SimpleServiceTest, methodNames)
{
    ASSERT_EQ("powerOfTwo"_sv, SimpleService::Service::methodNames()[0]);
}

TEST_F(SimpleServiceTest, powerOfTwo)
{
    Request request;

    request.setValue(13);
    Response response = client.powerOfTwoMethod(request);
    ASSERT_EQ(169, response.getValue());
    request.setValue(-13);
    response = movedClient.powerOfTwoMethod(request);
    ASSERT_EQ(169, response.getValue());
    request.setValue(2);
    response = client.powerOfTwoMethod(request);
    ASSERT_EQ(4, response.getValue());
    request.setValue(-2);
    response = movedClient.powerOfTwoMethod(request);
    ASSERT_EQ(4, response.getValue());
}

TEST_F(SimpleServiceTest, invalidServiceMethod)
{
    zserio::BlobBuffer<allocator_type> responseData;
    ASSERT_THROW(service.callMethod("nonexistentMethod"_sv, {}, responseData), zserio::ServiceException);
}

TEST_F(SimpleServiceTest, callWithContext)
{
    FakeContext fakeContext;
    ASSERT_FALSE(fakeContext.seenByService);
    Request request{10};
    Response response = client.powerOfTwoMethod(request, &fakeContext);
    ASSERT_EQ(100, response.getValue());
    ASSERT_TRUE(fakeContext.seenByService);

    fakeContext.seenByService = false;
    response = movedClient.powerOfTwoMethod(request, &fakeContext);
    ASSERT_EQ(100, response.getValue());
    ASSERT_TRUE(fakeContext.seenByService);
}

} // namespace simple_service
} // namespace service_types
