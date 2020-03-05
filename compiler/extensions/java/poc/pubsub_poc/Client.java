package pubsub_poc;

public class Client
{
    public Client(zserio_runtime.PubSubInterface pubsub)
    {
        this.pubsub = pubsub;
    }

    public void publishRequest(pubsub_poc.Int32Value zserioObject)
    {
        publishRequest(zserioObject, null);
    }

    public void publishRequest(pubsub_poc.Int32Value zserioObject, Object context)
    {
        publishZserioObject("pubsub/request", zserioObject, context);
    }

    public int subscribePowerOfTwo(zserio_runtime.PubSubCallback<UInt64Value> callback)
    {
        return subscribePowerOfTwo(callback, null);
    }

    public int subscribePowerOfTwo(zserio_runtime.PubSubCallback<UInt64Value> callback, Object context)
    {
        final String topic = "pubsub/powerOfTwo";
        final zserio_runtime.PubSubInterface.Callback uint64ValueCallback =
                new zserio_runtime.PubSubInterface.Callback()
                {
                    public void invoke(String topic, byte[] data)
                    {
                        onRawUInt64Value(callback, topic, data);
                    }
                };

        return pubsub.subscribe(topic, uint64ValueCallback, context);
    }

    public void unsubscribePowerOfTwo(int subscriptionId)
    {
        pubsub.unsubscribe(subscriptionId);
    }

    public int subscribeBooleanResponse(zserio_runtime.PubSubCallback<BoolValue> callback)
    {
        return subscribeBooleanResponse(callback, null);
    }

    public int subscribeBooleanResponse(zserio_runtime.PubSubCallback<BoolValue> callback, Object context)
    {
        final String topic = "pubsub/boolean/#";
        final zserio_runtime.PubSubInterface.Callback boolValueCallback =
                new zserio_runtime.PubSubInterface.Callback()
                {
                    public void invoke(String topic, byte[] data)
                    {
                        onRawBoolValue(callback, topic, data);
                    }
                };

        return pubsub.subscribe(topic, boolValueCallback, context);
    }

    public void unsubscribeBooleanResponse(int subscriptionId)
    {
        pubsub.unsubscribe(subscriptionId);
    }

    private void onRawBoolValue(zserio_runtime.PubSubCallback<BoolValue> callback, String topic, byte[] data)
    {
        final BoolValue object = zserio.runtime.io.ZserioIO.read(BoolValue.class, data);
        callback.invoke(topic, object);
    }

    private void onRawUInt64Value(zserio_runtime.PubSubCallback<UInt64Value> callback, String topic,
            byte[] data)
    {
        final UInt64Value object = zserio.runtime.io.ZserioIO.read(UInt64Value.class, data);
        callback.invoke(topic, object);
    }

    private <W extends zserio.runtime.io.Writer> void publishZserioObject(String topic, W zserioObject,
            Object context)
    {
        final byte[] data = zserio.runtime.io.ZserioIO.write(zserioObject);
        pubsub.publish(topic, data, context);
    }

    private final zserio_runtime.PubSubInterface pubsub;
}
