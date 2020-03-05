package zserio_pubsub_mqtt;

import java.util.UUID;

import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttException;

import zserio_runtime.PubSubInterface;
import zserio_runtime.PubSubException;

public class MqttClient implements PubSubInterface
{
    public MqttClient(String host, int port)
    {
        this.host = host;
        this.port = port;
    }

    public void publish(String topic, byte[] data, Object context)
    {
        try
        {
            final MqttConnectOptions options = new MqttConnectOptions();
            options.setAutomaticReconnect(true);
            options.setCleanSession(true);
            options.setConnectionTimeout(60);

            final String clientId = UUID.randomUUID().toString();

            final IMqttClient client = new org.eclipse.paho.client.mqttv3.MqttClient("tcp://" + host + ":" + port, clientId);
            client.connect(options);

            final MqttMessage message = new MqttMessage(data);
            client.publish(topic, message);

            client.disconnect();
            client.close();
        }
        catch (MqttException e)
        {
            throw new PubSubException(e.toString());
        }
    }

    public int subscribe(String topic, PubSubInterface.Callback callback, Object context)
    {
        final int subscriptionId = numIds++;
        subscriptions.put(subscriptionId,
                new MqttSubscription(host, port, subscriptionId, topic, callback, context));
        return subscriptionId;
    }

    public void unsubscribe(int subscriptionId)
    {
        final MqttSubscription subscription = subscriptions.remove(subscriptionId);
        if (subscription == null)
        {
            throw new zserio_runtime.PubSubException("MqttClient: Subscription '" + subscriptionId +
                    "' doesn't exist!");
        }
        subscription.close();
    }

    public void close()
    {
        for (MqttSubscription subscription: subscriptions.values())
            subscription.close();
    }

    private static class MqttSubscription
    {
        public MqttSubscription(String host, int port, int subscriptionId, String topic,
                PubSubInterface.Callback callback, Object context)
        {
            this.subscriptionId = subscriptionId;
            this.topic = topic;
            this.callback = callback;
            this.context = context;

            final MqttConnectOptions options = new MqttConnectOptions();
            options.setAutomaticReconnect(true);
            options.setCleanSession(true);
            options.setConnectionTimeout(60);

            final String clientId = UUID.randomUUID().toString();

            try
            {
                client = new org.eclipse.paho.client.mqttv3.MqttClient("tcp://" + host + ":" + port, clientId);
                client.connect(options);

                client.subscribe(topic,
                    new IMqttMessageListener()
                    {
                        public void messageArrived(String topic, MqttMessage message)
                        {
                            callback.invoke(topic, message.getPayload());
                        }
                    }
                );
            }
            catch (MqttException e)
            {
                throw new PubSubException(e.toString());
            }
        }

        public void close()
        {
            try
            {
                client.unsubscribe(topic);
                client.disconnect();
                client.close();
            }
            catch (MqttException e)
            {
                throw new PubSubException(e.toString());
            }
        }

        private final int subscriptionId;
        private final String topic;
        private final PubSubInterface.Callback callback;
        private final Object context;

        private final IMqttClient client;
    };

    private final String host;
    private final int port;

    private int numIds = 0;
    private java.util.Map<Integer, MqttSubscription> subscriptions =
            new java.util.HashMap<Integer, MqttSubscription>();
}
