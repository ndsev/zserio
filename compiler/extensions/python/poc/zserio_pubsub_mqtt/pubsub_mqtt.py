import paho.mqtt.client as mqtt
import paho.mqtt.publish as publish
import zserio_runtime

class MqttSubscription:
    def __init__(self, host, port, subscriptionId, topic, callback, context):
        self._subscriptionId = subscriptionId
        self._topic = topic
        self._callback = callback
        self._context = context

        self._client = mqtt.Client()
        self._client.on_connect = self._onConnect
        self._client.on_message = self._onMessage
        self._client.connect(host, port, 60)
        self._client.loop_start()

    def close(self):
        self._client.unsubscribe(self._topic)
        self._client.disconnect()
        self._client.loop_stop()

    def _onConnect(self, client, userdata, flags, rc):
        self._client.subscribe(self._topic)

    def _onMessage(self, client, userdata, msg):
        try:
            self._callback(self._subscriptionId, msg.topic, msg.payload)
        except Exception as e:
            print("MqttSubscription callback error:", e)

class MqttClient(zserio_runtime.PubSubInterface):
    def __init__(self, host, port):
        self._host = host
        self._port = port
        self._subscriptions = {}
        self._numIds = 0

    def publish(self, topic, data, context):
        publish.single(topic, payload=data, hostname=self._host, port=self._port)

    def subscribe(self, topic, callback, context):
        subscriptionId = self._numIds
        self._numIds += 1
        self._subscriptions[subscriptionId] = MqttSubscription(
            self._host, self._port, subscriptionId, topic, callback, context
        )
        return subscriptionId

    def unsubscribe(self, subscriptionId):
        self._subscriptions.pop(subscriptionId).close()
