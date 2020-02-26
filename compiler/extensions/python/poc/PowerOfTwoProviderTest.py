import zserio
import zserio_runtime
import pubsub_poc.api as api
import zserio_pubsub_mqtt

from functools import partial

if __name__ == "__main__":
    host = "localhost"
    port = 1883
    pubSubClient = zserio_pubsub_mqtt.MqttClient(host, port)

    powerOfTwoProvider = api.PowerOfTwoProvider(pubSubClient)

    def callback(topic, value):
        print("Calculaing power of two for:", value.getValue())

        uint64Value = api.UInt64Value.fromFields(value.getValue()**2)
        powerOfTwoProvider.publishPowerOfTwo(uint64Value)

    powerOfTwoProvider.subscribeRequest(callback)

    print("Power of two calculator, waiting for pubsub/request...")
    print("Press enter to quit.")

    input()
