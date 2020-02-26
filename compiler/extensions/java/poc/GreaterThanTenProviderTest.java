import java.util.Scanner;
import java.math.BigInteger;

import pubsub_poc.GreaterThanTenProvider;
import pubsub_poc.Int32Value;
import pubsub_poc.BoolValue;

import zserio_pubsub_mqtt.MqttClient;

public class GreaterThanTenProviderTest
{
    public static void main(String args[])
    {
        final String host = "localhost";
        final int port = 1883;

        final MqttClient mqttClient = new MqttClient(host, port);
        final GreaterThanTenProvider greaterThanTenProvider = new GreaterThanTenProvider(mqttClient);

        greaterThanTenProvider.subscribeRequest(
            new GreaterThanTenProvider.CallbackInt32Value()
            {
                public void invoke(String topic, Int32Value value)
                {
                    System.out.println("Checking if the value is greater than 10: " + value.getValue());

                    BoolValue boolValue = new BoolValue(value.getValue() > 10);
                    greaterThanTenProvider.publishGreaterThanTen(boolValue);
                }
            }
        );

        System.out.println("Greater than 10 checker, waiting for pubsub/request...");
        System.out.println("Press enter to quit.");

        final Scanner scanner = new Scanner(System.in);
        scanner.hasNextLine();
    }
}
