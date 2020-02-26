package pubsub_poc;

public class Client
{
    public Client(zserio_runtime.PubSubInterface client)
    {
        this.client = client;
    }

    public void publishRequest(pubsub_poc.Int32Value zserioObject)
    {
        publishRequest(zserioObject, null);
    }

    public void publishRequest(pubsub_poc.Int32Value zserioObject, Object context)
    {
        publishZserioObject("pubsub/request", zserioObject, context);
    }

    public int subscribePowerOfTwo(CallbackUInt64Value callback)
    {
        return subscribePowerOfTwo(callback, null);
    }

    public int subscribePowerOfTwo(CallbackUInt64Value callback, Object context)
    {
        final String topic = "pubsub/powerOfTwo";
        final zserio_runtime.PubSubInterface.Callback uint64ValueCallback =
                new zserio_runtime.PubSubInterface.Callback()
                {
                    public void invoke(int subscriptionId, String topic, byte[] data)
                    {
                        onRawUInt64Value(subscriptionId, topic, data);
                    }
                };

        final int subscriptionId = client.subscribe(topic, uint64ValueCallback, context);
        subscribersUInt64Value.put(subscriptionId, callback);
        return subscriptionId;
    }

    public void unsubscribePowerOfTwo(int subscriptionId)
    {
        client.unsubscribe(subscriptionId);
        subscribersUInt64Value.remove(subscriptionId);
    }

    public int subscribeBooleanResponse(CallbackBoolValue callback)
    {
        return subscribeBooleanResponse(callback, null);
    }

    public int subscribeBooleanResponse(CallbackBoolValue callback, Object context)
    {
        final String topic = "pubsub/boolean/#";
        final zserio_runtime.PubSubInterface.Callback boolValueCallback =
                new zserio_runtime.PubSubInterface.Callback()
                {
                    public void invoke(int subscriptionId, String topic, byte[] data)
                    {
                        onRawBoolValue(subscriptionId, topic, data);
                    }
                };

        final int subscriptionId = client.subscribe(topic, boolValueCallback, context);
        subscribersBoolValue.put(subscriptionId, callback);
        return subscriptionId;
    }

    public void unsubscribeBooleanResponse(int subscriptionId)
    {
        client.unsubscribe(subscriptionId);
        subscribersBoolValue.remove(subscriptionId);
    }

    private void onRawBoolValue(int subscriptionId, String topic, byte[] data)
    {
        final CallbackBoolValue callback = subscribersBoolValue.get(subscriptionId);
        if (callback == null)
        {
            throw new zserio_runtime.PubSubException("pubsub_poc.Client: Unknown subscription '" +
                    subscriptionId + "' for 'BoolValue'");
        }

        final BoolValue object = zserio.runtime.io.ZserioIO.read(BoolValue.class, data);
        callback.invoke(topic, object);
    }

    private void onRawUInt64Value(int subscriptionId, String topic, byte[] data)
    {
        final CallbackUInt64Value callback = subscribersUInt64Value.get(subscriptionId);
        if (callback == null)
        {
            throw new zserio_runtime.PubSubException("pubsub_poc.Client: Unknown subscription '" +
                    subscriptionId + "' for 'UInt64Value'");
        }

        final UInt64Value object = zserio.runtime.io.ZserioIO.read(UInt64Value.class, data);
        callback.invoke(topic, object);
    }

    private <W extends zserio.runtime.io.Writer> void publishZserioObject(String topic, W zserioObject,
            Object context)
    {
        final byte[] data = zserio.runtime.io.ZserioIO.write(zserioObject);
        client.publish(topic, data, context);
    }

    public interface CallbackBoolValue
    {
        void invoke(String topic, pubsub_poc.BoolValue object);
    }

    public interface CallbackUInt64Value
    {
        void invoke(String topic, pubsub_poc.UInt64Value object);
    }

    private final zserio_runtime.PubSubInterface client;
    private final java.util.Map<Integer, CallbackBoolValue> subscribersBoolValue =
            new java.util.HashMap<Integer, CallbackBoolValue>();
    private final java.util.Map<Integer, CallbackUInt64Value> subscribersUInt64Value =
            new java.util.HashMap<Integer, CallbackUInt64Value>();
}
