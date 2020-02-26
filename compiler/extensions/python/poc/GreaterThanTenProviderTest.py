import zserio
import zserio_runtime
import pubsub_poc.api as api
import zserio_pubsub_mqtt

from functools import partial

if __name__ == "__main__":
    host = "localhost"
    port = 1883
    pubSubClient = zserio_pubsub_mqtt.MqttClient(host, port)

    greaterThanTenProvider = api.GreaterThanTenProvider(pubSubClient)

    def callback(topic, value):
        print("Checking if the value is greater than 10:", value.getValue())

        boolValue = api.BoolValue.fromFields(value.getValue() > 10)
        greaterThanTenProvider.publishGreaterThanTen(boolValue)

    greaterThanTenProvider.subscribeRequest(callback)

    print("Greater than 10 checker, waiting for pubsub/request...")
    print("Press enter to quit.")

    input()
