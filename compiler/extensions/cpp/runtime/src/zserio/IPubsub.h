#ifndef ZSERIO_IPUBSUB_H_INC
#define ZSERIO_IPUBSUB_H_INC

#include <functional>
#include <vector>
#include <string>
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
    using OnTopic = std::function<void(
            SubscriptionId id, const std::string& topic, const std::vector<uint8_t>& data)>;

    /**
     * Publishes given data as a specified topic.
     *
     * \param topic Topic definition.
     * \param data Data to publish.
     * \param context Context specific for a particular Pub/Sub implementation.
     *
     * \throw PubsubException when publishing fails.
     */
    virtual void publish(const std::string& topic, const std::vector<uint8_t>& data, void* context) = 0;

    /**
     * Reserves subscription ID for a new subscription.
     *
     * The ID is released back to the Pub/Sub once unsubscribe is called.
     *
     * \return Reserved subscription ID.
     */
    virtual SubscriptionId reserveId() = 0;

    /**
     * Subscribes a topic.
     *
     * \param id Subscrition ID to use.
     * \param topic Topic definition to subscribe. Note that the definition format depends on the particular
     *              Pub/Sub backend implementation and therefore e.g. wildcards can be used only if they are
     *              supported by Pub/Sub backend.
     * \param callback Callback to be called when a message with the specified topic arrives.
     * \param context Context specific for a particular Pub/Sub implementation.
     *
     * \throw PubsubException when subscribing fails.
     */
    virtual void subscribe(SubscriptionId id, const std::string& topic, const OnTopic& callback,
            void* context) = 0;

    /**
     * Unsubscribes the subscription under the given ID (if any) and releases the reserved ID.
     *
     * \note Unsubscribe can be safely called to release the reserved ID even if no subscription was done.
     *
     * \param id ID of the subscription to be unsubscribed.
     *
     * \throw PubsubException when unsubscribing fails.
     */
    virtual void unsubscribe(SubscriptionId id) = 0;
};

} // namespace zserio

#endif // ZSERIO_IPUBSUB_H_INC
