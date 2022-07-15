#ifndef TEST_UTILS_TEST_PUBSUB_H_INC
#define TEST_UTILS_TEST_PUBSUB_H_INC

#include "zserio/AllocatorHolder.h"
#include "zserio/RebindAlloc.h"
#include "zserio/IPubsub.h"
#include "zserio/PubsubException.h"

namespace test_utils
{

template <typename ALLOC>
class TestPubsub : public zserio::IPubsub, public zserio::AllocatorHolder<ALLOC>
{
public:
    TestPubsub(const ALLOC& allocator = ALLOC()) :
            zserio::AllocatorHolder<ALLOC>(allocator),
            m_subscriptions(allocator)
    {}

    void publish(zserio::StringView topic, zserio::Span<const uint8_t> data, void* context) override
    {
        processPublishContext(context);

        for (const auto& subscription : m_subscriptions)
        {
            if (subscription.second.topic == topic)
                subscription.second.callback->operator()(topic, data);
        }
    }

    SubscriptionId subscribe(zserio::StringView topic, const std::shared_ptr<OnTopicCallback>& callback,
            void *context) override
    {
        processSubscribeContext(context);

        m_subscriptions.emplace(m_numIds, Subscription{topic, callback});
        return m_numIds++;
    }

    void unsubscribe(SubscriptionId id) override
    {
        const auto found = m_subscriptions.find(id);
        if (found == m_subscriptions.end())
            throw zserio::PubsubException("TestPubsub: Invalid subscription ID '") << id << "'!";
        m_subscriptions.erase(found);
    }

protected:
    virtual void processPublishContext(void*) {}
    virtual void processSubscribeContext(void*) {}

private:
    SubscriptionId m_numIds = 0;

    struct Subscription
    {
        zserio::StringView topic;
        std::shared_ptr<OnTopicCallback> callback;
    };

    template <typename KEY, typename T>
    using map_type = std::map<KEY, T, std::less<KEY>, zserio::RebindAlloc<ALLOC, std::pair<const KEY, T>>>;

    map_type<SubscriptionId, Subscription> m_subscriptions;
};

} // namespace test_utils

#endif // TEST_UTILS_TEST_PUBSUB_H_INC

