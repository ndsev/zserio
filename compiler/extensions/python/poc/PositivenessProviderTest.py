import zserio
import zserio_runtime
import pubsub_poc.api as api
import zserio_pubsub_mqtt

from functools import partial

if __name__ == "__main__":
    host = "localhost"
    port = 1883
    pubSubClient = zserio_pubsub_mqtt.MqttClient(host, port)

    positivenessProvider = api.PositivenessProvider(pubSubClient)

    def callback(topic, value):
        print("Checking positiveness of the value:", value.getValue())

        boolValue = api.BoolValue.fromFields(value.getValue() >= 0)
        positivenessProvider.publishPositiveness(boolValue)

    positivenessProvider.subscribeRequest(callback)

    print("Positiveness checker, waiting for pubsub/request...")
    print("Press enter to quit.")

    input()
