#include <fstream>
#include <memory>

#include "gtest/gtest.h"
#include "service_types/simple_service/SimpleService.h"
#include "test_utils/LocalServiceClient.h"
#include "zserio/RebindAlloc.h"
#include "zserio/SerializeUtil.h"
#include "zserio/ServiceException.h"

using namespace zserio::literals;

namespace service_types
{
namespace simple_service
{

using allocator_type = SimpleService::Client::allocator_type;
using LocalServiceClient = test_utils::LocalServiceClient<allocator_type>;
template <typename T>
using vector_type = zserio::vector<T, allocator_type>;
using BitBuffer = zserio::BasicBitBuffer<allocator_type>;

namespace
{
struct FakeContext
{
    bool seenByService = false;
};
} // namespace

class SimpleServiceImpl : public SimpleService::Service
{
public:
    Response powerOfTwoImpl(const Request& request, void* context) override
    {
        if (context != nullptr)
        {
            static_cast<FakeContext*>(context)->seenByService = true;
        }

        int32_t value = request.getValue();
        if (value < 0)
        {
            value = -value;
        }
        return Response(static_cast<uint64_t>(value) * static_cast<uint64_t>(value), get_allocator_ref());
    }

    vector_type<uint8_t> powerOfTwoRawImpl(zserio::Span<const uint8_t> requestData, void* context) override
    {
        zserio::BitStreamReader reader(requestData.data(), requestData.size());
        Request request(reader);
        Response response = powerOfTwoImpl(request, context);

        vector_type<uint8_t> responseData;
        responseData.resize((response.bitSizeOf() + 7) / 8);
        zserio::BitStreamWriter writer(responseData.data(), responseData.size());
        response.write(writer);

        return responseData;
    }
};

class SimpleServiceTest : public ::testing::Test
{
public:
    SimpleServiceTest() :
            localServiceClient(service),
            client(localServiceClient),
            movedService(createService()),
            movedLocalServiceClient(movedService),
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
        SimpleService::Client createdClient(movedLocalServiceClient);
        return createdClient;
    }

protected:
    SimpleServiceImpl service;
    LocalServiceClient localServiceClient;
    SimpleService::Client client;
    SimpleServiceImpl movedService;
    LocalServiceClient movedLocalServiceClient;
    SimpleService::Client movedClient;
};

TEST_F(SimpleServiceTest, serviceFullName)
{
    ASSERT_EQ("service_types.simple_service.SimpleService"_sv, SimpleService::Service::serviceFullName());
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

TEST_F(SimpleServiceTest, powerOfTwoRaw)
{
    Request request;
    request.setValue(13);
    auto requestData = zserio::serialize(request);
    auto responseData = client.powerOfTwoRawMethod(requestData.getBytes());
    auto response = zserio::deserialize<Response>(BitBuffer(responseData));
    ASSERT_EQ(169, response.getValue());
}

TEST_F(SimpleServiceTest, invalidServiceMethod)
{
    ASSERT_THROW(service.callMethod("nonexistentMethod"_sv, {}, nullptr), zserio::ServiceException);
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
