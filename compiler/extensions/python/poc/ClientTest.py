import zserio
import zserio_runtime
import pubsub_poc.api as api
import zserio_pubsub_mqtt

from functools import partial

if __name__ == "__main__":
    host = "localhost"
    port = 1883
    pubSubClient = zserio_pubsub_mqtt.MqttClient(host, port)

    client = api.Client(pubSubClient)

    powerOfTwoCallback = lambda topic, value: print("power of two:", value.getValue())
    powerOfTwoId = client.subscribePowerOfTwo(powerOfTwoCallback)
    powerOfTwoSubscribed = True

    booleanResponseCallback = lambda topic, value: print(topic + ":", "true" if value.getValue() else "false")
    booleanResponseId = client.subscribeBooleanResponse(booleanResponseCallback)
    booleanResponseSubscribed = True

    while True:
        line = input()

        if not line:
            continue

        if line[0] == 'q':
            exit(0)

        if line[0] == 'u':
            if powerOfTwoSubscribed:
                client.unsubscribePowerOfTwo(powerOfTwoId)
                powerOfTwoSubscribed = False
            else:
                powerOfTwoId = client.subscribePowerOfTwo(powerOfTwoCallback)
                powerOfTwoSubscribed = True
            print("power of two", "enabled" if powerOfTwoSubscribed else "disabled")
            continue

        if line[0] == 'b':
            if booleanResponseSubscribed:
                client.unsubscribeBooleanResponse(booleanResponseId)
                booleanResponseSubscribed = False
            else:
                booleanResponseId = client.subscribeBooleanResponse(booleanResponseCallback)
                booleanResponseSubscribed = True
            print("boolean response", "enabled" if booleanResponseSubscribed else "disabled")
            continue

        int32Value = api.Int32Value.fromFields(int(line))
        client.publishRequest(int32Value)
