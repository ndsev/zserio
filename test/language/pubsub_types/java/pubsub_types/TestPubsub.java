package pubsub_types;

import java.util.Map;
import java.util.HashMap;
import zserio.runtime.pubsub.PubsubInterface;
import zserio.runtime.pubsub.PubsubException;

public class TestPubsub implements PubsubInterface
{
    public static class TestPubsubContext
    {
        public boolean seenByPubsub = false;
    }

    @Override
    public void publish(String topic, byte[] data, Object context)
    {
        if (context != null)
            ((TestPubsubContext)context).seenByPubsub = true;

        for (Subscription subscription : subscriptions.values())
        {
            if (subscription.topic.equals(topic))
                subscription.callback.invoke(topic, data);
        }
    }

    @Override
    public int subscribe(String topic, Callback callback, Object context)
    {
        if (context != null)
            ((TestPubsubContext)context).seenByPubsub = true;

        subscriptions.put(numIds, new Subscription(topic, callback));
        return numIds++;
    }

    @Override
    public void unsubscribe(int subscriptionId)
    {
        final Subscription removed = subscriptions.remove(subscriptionId);
        if (removed == null)
            throw new PubsubException("TestPubsub: Invalid subscription ID '" + subscriptionId + "'!");
    }

    private static class Subscription
    {
        public Subscription(String topic, Callback callback)
        {
            this.topic = topic;
            this.callback = callback;
        }

        public final String topic;
        public final Callback callback;
    }

    private int numIds = 0;
    private final Map<Integer, Subscription> subscriptions = new HashMap<Integer, Subscription>();
}
