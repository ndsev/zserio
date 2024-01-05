package zserio.runtime.pubsub;

/** Interface for Pub/Sub client backends. */
public interface PubsubInterface
{
    /**
     * Publishes given data as a specified topic.
     *
     * @param topic Topic definition.
     * @param data Data to publish.
     * @param context Context specific for a particular Pub/Sub implementation.
     */
    public void publish(String topic, byte[] data, Object context);

    /**
     * Subscribes a topic.
     *
     * @param topic Topic definition to subscribe. Note that the definition format depends on the particular
     *              Pub/Sub backend implementation and therefore e.g. wildcards can be used only if they are
     *              supported by Pub/Sub backend.
     * @param callback Callback to be called when a message with the specified topic arrives.
     * @param context Context specific for a particular Pub/Sub implementation.
     *
     * @return Subscription ID.
     */
    public int subscribe(String topic, Callback callback, Object context);

    /**
     * Unsubscribes the subscription with the given ID.
     *
     * @param subscriptionId ID of the subscription to be unsubscribed.
     */
    public void unsubscribe(int subscriptionId);

    /**
     * Callback passed to the subscribe method and called when a message
     * with subscribed topic arrives.
     */
    public interface Callback
    {
        /**
         * Invokes the callback.
         *
         * @param topic Topic associated with the message.
         * @param data Message's data as a byte array.
         */
        void invoke(String topic, byte[] data);
    }
}
