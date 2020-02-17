#include <map>
#include <string>
#include <iostream>

#include "zserio_runtime/IPubSubClient.h"
#include "pubsub_poc/SimplePubSub.h"

class TestPubSub : public zserio::IPubSubClient
{
public:
    virtual void publish(const std::string& topic, const std::vector<uint8_t>& data, void* context) override
    {
        for (auto it = m_subscriptions.begin(); it != m_subscriptions.end(); ++it)
        {
            if (topic == it->second.topic)
                it->second.callback(topic, data);
        }
    }

    virtual SubscriptionId subscribe(const std::string& topic, const OnTopic& callback, void* context) override
    {
        m_subscriptions.insert({numIds, {topic, callback}});
        return numIds++;
    }

    virtual void unsubscribe(SubscriptionId id) override
    {
        m_subscriptions.erase(id);
    }

private:
    struct Subscription
    {
        std::string topic;
        OnTopic callback;
    };

    std::map<SubscriptionId, Subscription> m_subscriptions;
    SubscriptionId numIds;
};

int main(int argc, char* argv[])
{
    TestPubSub testPubSub;
    pubsub_poc::SimplePubSub simplePubSub1(testPubSub);
    pubsub_poc::SimplePubSub simplePubSub2(testPubSub);
    pubsub_poc::SimplePubSub simplePubSub3(testPubSub);

    auto id2 = simplePubSub2.subscribeInt32ValueIn(
        [](const std::string& topic, const pubsub_poc::Int32Value& value)
        {
            std::cout << "simplePubSub2 got int32 value: topic=" << topic << ", value=" << value.getValue()
                        << std::endl;
        }
    );

    auto id3 = simplePubSub3.subscribeInt32ValueIn(
        [](const std::string& topic, const pubsub_poc::Int32Value& value)
        {
            std::cout << "simplePubSub3 got int32 value: topic=" << topic << ", value=" << value.getValue()
                        << std::endl;
        }
    );

    // not subscribed, should be ignored
    pubsub_poc::UInt64Value uint64Value{123456789};
    simplePubSub1.publishUInt64Value(uint64Value);

    // subscribed, should be processed by both simplePubSub2 and simplePubSub3
    pubsub_poc::Int32Value int32Value{987654321};
    simplePubSub1.publishInt32ValueOut(int32Value);

    simplePubSub2.unsubscribeInt32ValueIn(id2);
    // should be processed only by simplePubSub3
    int32Value.setValue(13);
    simplePubSub1.publishInt32ValueOut(int32Value);

    simplePubSub3.unsubscribeInt32ValueIn(id3);

    return 0;
}
