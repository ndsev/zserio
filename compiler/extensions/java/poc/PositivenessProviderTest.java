import java.util.Scanner;

import pubsub_poc.PositivenessProvider;
import pubsub_poc.Int32Value;
import pubsub_poc.BoolValue;

import zserio_pubsub_mqtt.MqttClient;

public class PositivenessProviderTest
{
    public static void main(String args[])
    {
        final String host = "localhost";
        final int port = 1883;

        final MqttClient mqttClient = new MqttClient(host, port);
        final PositivenessProvider positivenessProvider = new PositivenessProvider(mqttClient);

        positivenessProvider.subscribeRequest(
            new PositivenessProvider.CallbackInt32Value()
            {
                public void invoke(String topic, Int32Value value)
                {
                    System.out.println("Checking positiveness of the value: " + value.getValue());

                    BoolValue boolValue = new BoolValue(value.getValue() >= 0);
                    positivenessProvider.publishPositiveness(boolValue);
                }
            }
        );

        System.out.println("Positiveness checker, waiting for pubsub/request...");
        System.out.println("Press enter to quit.");

        final Scanner scanner = new Scanner(System.in);
        scanner.hasNextLine();

        mqttClient.close();
    }
}
