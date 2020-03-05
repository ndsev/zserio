package pubsub_poc;

public class PowerOfTwoProvider
{
    public PowerOfTwoProvider(zserio_runtime.PubSubInterface client)
    {
        this.client = client;
    }

    public void publishPowerOfTwo(pubsub_poc.UInt64Value zserioObject)
    {
        publishPowerOfTwo(zserioObject, null);
    }

    public void publishPowerOfTwo(pubsub_poc.UInt64Value zserioObject, Object context)
    {
        publishZserioObject("pubsub/powerOfTwo", zserioObject, context);
    }

    public int subscribeRequest(CallbackInt32Value callback)
    {
        return subscribeRequest(callback, null);
    }

    public int subscribeRequest(CallbackInt32Value callback, Object context)
    {
        final String topic = "pubsub/request";
        final zserio_runtime.PubSubInterface.Callback int32ValueCallback =
                new zserio_runtime.PubSubInterface.Callback()
                {
                    public void invoke(String topic, byte[] data)
                    {
                        onRawInt32Value(callback, topic, data);
                    }
                };

        return client.subscribe(topic, int32ValueCallback, context);
    }

    public void unsubscribeRequest(int subscriptionId)
    {
        client.unsubscribe(subscriptionId);
    }

    private void onRawInt32Value(CallbackInt32Value callback, String topic, byte[] data)
    {
        final pubsub_poc.Int32Value object = zserio.runtime.io.ZserioIO.read(pubsub_poc.Int32Value.class, data);
        callback.invoke(topic, object);
    }

    private <W extends zserio.runtime.io.Writer> void publishZserioObject(String topic, W zserioObject,
            Object context)
    {
        final byte[] data = zserio.runtime.io.ZserioIO.write(zserioObject);
        client.publish(topic, data, context);
    }

    public interface CallbackInt32Value
    {
        void invoke(String topic, pubsub_poc.Int32Value object);
    }

    private final zserio_runtime.PubSubInterface client;
}
