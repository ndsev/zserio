package zserio.runtime.pubsub;

/**
 * Generic callback invoked by generated Pub/Sub classes on subscriptions.
 */
public interface PubsubCallback<T>
{
    /**
     * Invokes the user callback.
     *
     * @param topic Topic associated with the message.
     * @param message Message as a Zserio generated type - e.g. structure.
     */
    public void invoke(String topic, T message);
};
