import java.util.Scanner;

import pubsub_poc.Client;
import pubsub_poc.Int32Value;
import pubsub_poc.BoolValue;
import pubsub_poc.UInt64Value;

import zserio_pubsub_mqtt.MqttClient;

public class ClientTest
{
    public static void main(String args[])
    {
        final String host = "localhost";
        final int port = 1883;

        final MqttClient mqttClient = new MqttClient(host, port);
        final Client client = new Client(mqttClient);

        final zserio_runtime.PubSubCallback<BoolValue> booleanResponseCallback =
            new zserio_runtime.PubSubCallback<BoolValue>()
            {
                public void invoke(String topic, BoolValue value)
                {
                    System.out.println(topic + ":" + (value.getValue() ? "true" : "false"));
                }
            };
        int booleanResponseId = client.subscribeBooleanResponse(booleanResponseCallback);
        boolean booleanResponseSubscribed = true;

        final zserio_runtime.PubSubCallback<UInt64Value> powerOfTwoCallback =
            new zserio_runtime.PubSubCallback<UInt64Value>()
            {
                public void invoke(String topic, UInt64Value value)
                {
                    System.out.println("powerOfTwo:" + value.getValue().toString());
                }
            };

        int powerOfTwoId = client.subscribePowerOfTwo(powerOfTwoCallback);
        boolean powerOfTwoSubscribed = true;

        final Scanner scanner = new Scanner(System.in);

        while (true)
        {
            if (!scanner.hasNextLine())
                break;

            final String input = scanner.nextLine();
            if (input.isEmpty())
                continue;

            if (input.charAt(0) == 'q')
                break;

            if (input.charAt(0) == 'u')
            {
                if (powerOfTwoSubscribed)
                {
                    client.unsubscribePowerOfTwo(powerOfTwoId);
                    powerOfTwoSubscribed = false;
                }
                else
                {
                    powerOfTwoId = client.subscribePowerOfTwo(powerOfTwoCallback);
                    powerOfTwoSubscribed = true;
                }
                System.out.println("power of two " + (powerOfTwoSubscribed ? "enabled": "disabled"));
                continue;
            }

            if (input.charAt(0) == 'b')
            {
                if (booleanResponseSubscribed)
                {
                    client.unsubscribeBooleanResponse(booleanResponseId);
                    booleanResponseSubscribed = false;
                }
                else
                {
                    booleanResponseId = client.subscribeBooleanResponse(booleanResponseCallback);
                    booleanResponseSubscribed = true;
                }
                System.out.println("boolean response " + (booleanResponseSubscribed ? "enabled": "disabled"));
                continue;
            }

            final int value = Integer.parseInt(input);
            Int32Value int32Value = new Int32Value(value);
            client.publishRequest(int32Value);
        }

        mqttClient.close();
    }
}
