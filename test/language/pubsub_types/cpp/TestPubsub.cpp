#include "TestPubsub.h"
#include "zserio/PubsubException.h"

namespace pubsub_types
{

void TestPubsub::publish(const std::string& topic, const std::vector<uint8_t>& data, void* context)
{
    if (context != nullptr)
        static_cast<Context*>(context)->seenByPubsub = true;

    for (const auto& subscription : m_subscriptions)
    {
        if (subscription.second.topic == topic)
            subscription.second.callback(topic, data);
    }
}

zserio::IPubsub::SubscriptionId TestPubsub::subscribe(const std::string& topic, const OnTopic& callback, void *context)
{
    if (context != nullptr)
        static_cast<Context*>(context)->seenByPubsub = true;

    m_subscriptions.emplace(m_numIds, Subscription{topic, callback});
    return m_numIds++;
}

void TestPubsub::unsubscribe(SubscriptionId id)
{
    const auto found = m_subscriptions.find(id);
    if (found == m_subscriptions.end())
        throw zserio::PubsubException("TestPubsub: Invalid subscription ID '" + std::to_string(id) + "'!");
    m_subscriptions.erase(found);
}

} // namespace pubsub_types
