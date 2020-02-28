#include "TestPubsub.h"

namespace pubsub_types
{



void TestPubsub::publish(const std::string& topic, const std::vector<uint8_t>& data, void* context)
{
    if (context != nullptr)
        static_cast<Context*>(context)->seenByPubsub = true;

    for (const auto& subscription : m_subscriptions)
    {
        if (subscription.second.topic == topic)
            subscription.second.callback(subscription.first, topic, data);
    }
}

zserio::IPubsub::SubscriptionId TestPubsub::reserveId()
{
    return m_numIds++;
}

void TestPubsub::subscribe(SubscriptionId id, const std::string& topic, const OnTopic& callback, void *context)
{
    if (context != nullptr)
        static_cast<Context*>(context)->seenByPubsub = true;

    m_subscriptions.emplace(id, Subscription{topic, callback});
}

void TestPubsub::unsubscribe(SubscriptionId id)
{
    m_subscriptions.erase(id);
}

} // namespace pubsub_types
