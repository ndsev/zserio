#ifndef PUBSUB_TYPES_TEST_PUBSUB_H
#define PUBSUB_TYPES_TEST_PUBSUB_H

#include <map>
#include "zserio/IPubsub.h"

namespace pubsub_types
{

class TestPubsub : public zserio::IPubsub
{
public:
    void publish(const std::string& topic, const std::vector<uint8_t>& data, void* context) override;
    SubscriptionId reserveId() override;
    void subscribe(SubscriptionId id, const std::string& topic, const OnTopic& callback,
            void *context) override;
    void unsubscribe(SubscriptionId id) override;

    struct Context
    {
        bool seenByPubsub;
    };

private:
    SubscriptionId m_numIds = 0;

    struct Subscription
    {
        std::string topic;
        OnTopic callback;
    };
    std::map<SubscriptionId, Subscription> m_subscriptions;
};

} // namespace pubsub_types

#endif // PUBSUB_TYPES_TEST_PUBSUB_H
