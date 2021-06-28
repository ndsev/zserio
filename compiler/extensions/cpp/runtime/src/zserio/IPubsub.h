#ifndef ZSERIO_IPUBSUB_H_INC
#define ZSERIO_IPUBSUB_H_INC

#include <memory>
#include "zserio/StringView.h"
#include "zserio/Span.h"
#include "zserio/Types.h"

namespace zserio
{

/** Interface for Pub/Sub client backends. */
class IPubsub
{
public:
    virtual ~IPubsub() = default;

    /**
     * Subscription ID which is unique for each single subscription.
     */
    using SubscriptionId = size_t;

    /**
     * OnTopic callback which invoked for subscribed messages.
     */
    class OnTopicCallback
    {
    public:
        virtual ~OnTopicCallback() = default;
        virtual void operator()(StringView topic, Span<const uint8_t> data) = 0;
    };

    /**
     * Publishes given data as a specified topic.
     *
     * \param topic Topic definition.
     * \param data Data to publish.
     * \param context Context specific for a particular Pub/Sub implementation.
     *
     * \throw PubsubException when publishing fails.
     */
    virtual void publish(StringView topic, Span<const uint8_t> data, void* context) = 0;

    /**
     * Subscribes a topic.
     *
     * \param topic Topic definition to subscribe. Note that the definition format depends on the particular
     *              Pub/Sub backend implementation and therefore e.g. wildcards can be used only if they are
     *              supported by Pub/Sub backend.
     * \param callback Callback to be called when a message with the specified topic arrives.
     * \param context Context specific for a particular Pub/Sub implementation.
     *
     * \return Subscription ID.
     * \throw PubsubException when subscribing fails.
     */
    virtual SubscriptionId subscribe(
            StringView topic, const std::shared_ptr<OnTopicCallback>& callback, void* context) = 0;

    /**
     * Unsubscribes the subscription with the given ID.
     *
     * \param id ID of the subscription to be unsubscribed.
     *
     * \throw PubsubException when unsubscribing fails.
     */
    virtual void unsubscribe(SubscriptionId id) = 0;
};

} // namespace zserio

#endif // ZSERIO_IPUBSUB_H_INC
