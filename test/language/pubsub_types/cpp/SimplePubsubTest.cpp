#include "gtest/gtest.h"

#include "TestPubsub.h"

#include "pubsub_types/simple_pubsub/SimplePubsubProvider.h"
#include "pubsub_types/simple_pubsub/SimplePubsubClient.h"
#include "pubsub_types/simple_pubsub/SimplePubsub.h"

namespace pubsub_types
{
namespace simple_pubsub
{

class SimplePubsubTest : public ::testing::Test
{
public:
    SimplePubsubTest()
    :   simplePubsubProvider(pubsub),
        simplePubsubClient(pubsub),
        simplePubsub(pubsub)
    {}

private:
    TestPubsub pubsub;

protected:
    SimplePubsubProvider simplePubsubProvider;
    SimplePubsubClient simplePubsubClient;
    SimplePubsub simplePubsub;
};

TEST_F(SimplePubsubTest, powerOfTwoClientAndProvider)
{
    simplePubsubProvider.subscribeRequest(
        [this](const std::string& topic, const Int32Value& value)
        {
            ASSERT_EQ("simple_pubsub/request", topic);
            const uint64_t absValue = value.getValue() > 0
                    ? static_cast<uint64_t>(value.getValue())
                    : static_cast<uint64_t>(-value.getValue());
            UInt64Value result{absValue * absValue};
            simplePubsubProvider.publishPowerOfTwo(result);
        }
    );

    uint64_t result = 0;
    simplePubsubClient.subscribePowerOfTwo(
        [&result](const std::string& topic, const UInt64Value& value)
        {
            ASSERT_EQ("simple_pubsub/power_of_two", topic);
            result = value.getValue();
        }
    );

    Int32Value request{13};
    simplePubsubClient.publishRequest(request);
    ASSERT_EQ(169, result);

    request.setValue(-13);
    simplePubsubClient.publishRequest(request);
    ASSERT_EQ(169, result);

    request.setValue(2);
    simplePubsubClient.publishRequest(request);
    ASSERT_EQ(4, result);

    request.setValue(-2);
    simplePubsubClient.publishRequest(request);
    ASSERT_EQ(4, result);
}

TEST_F(SimplePubsubTest, powerOfTwoSimplePubsub)
{
    simplePubsub.subscribeRequest(
        [this](const std::string& topic, const Int32Value& value)
        {
            ASSERT_EQ("simple_pubsub/request", topic);
            const uint64_t absValue = value.getValue() > 0
                    ? static_cast<uint64_t>(value.getValue())
                    : static_cast<uint64_t>(-value.getValue());
            UInt64Value result{absValue * absValue};
            simplePubsub.publishPowerOfTwo(result);
        }
    );

    uint64_t result = 0;
    simplePubsub.subscribePowerOfTwo(
        [&result](const std::string& topic, const UInt64Value& value)
        {
            ASSERT_EQ("simple_pubsub/power_of_two", topic);
            result = value.getValue();
        }
    );

    Int32Value request{13};
    simplePubsub.publishRequest(request);
    ASSERT_EQ(169, result);

    request.setValue(-13);
    simplePubsub.publishRequest(request);
    ASSERT_EQ(169, result);

    request.setValue(2);
    simplePubsub.publishRequest(request);
    ASSERT_EQ(4, result);

    request.setValue(-2);
    simplePubsub.publishRequest(request);
    ASSERT_EQ(4, result);
}

TEST_F(SimplePubsubTest, publishRequestWithContext)
{
    TestPubsub::Context context;
    ASSERT_FALSE(context.seenByPubsub);
    Int32Value request{42};
    simplePubsub.publishRequest(request, &context);
    ASSERT_TRUE(context.seenByPubsub);
}

TEST_F(SimplePubsubTest, subscribeRequestWithContext)
{
    TestPubsub::Context context;
    ASSERT_FALSE(context.seenByPubsub);
    simplePubsub.subscribeRequest([](const std::string&, const Int32Value&){}, &context);
    ASSERT_TRUE(context.seenByPubsub);
}

} // namespace simple_pubsub
} // namespace pubsub_types
