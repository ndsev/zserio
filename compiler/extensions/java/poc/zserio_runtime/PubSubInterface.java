package zserio_runtime;

public interface PubSubInterface
{
    public void publish(String topic, byte[] data, Object context);

    public int subscribe(String topic, Callback callback, Object context);

    public void unsubscribe(int subscriptionId);

    public interface Callback
    {
        void invoke(int subscriptionId, String topic, byte[] data);
    };
};
