import java.util.Scanner;
import java.math.BigInteger;

import pubsub_poc.PowerOfTwoProvider;
import pubsub_poc.Int32Value;
import pubsub_poc.UInt64Value;

import zserio_pubsub_mqtt.MqttClient;

public class PowerOfTwoProviderTest
{
    public static void main(String args[])
    {
        final String host = "localhost";
        final int port = 1883;

        final MqttClient mqttClient = new MqttClient(host, port);
        final PowerOfTwoProvider powerOfTwoProvider = new PowerOfTwoProvider(mqttClient);

        powerOfTwoProvider.subscribeRequest(
            new PowerOfTwoProvider.CallbackInt32Value()
            {
                public void invoke(String topic, Int32Value value)
                {
                    System.out.println("Calculating power of two for: " + value.getValue());

                    UInt64Value uint64Value = new UInt64Value(BigInteger.valueOf(value.getValue()).pow(2));
                    powerOfTwoProvider.publishPowerOfTwo(uint64Value);
                }
            }
        );

        System.out.println("Power of two calculator, waiting for pubsub/request...");
        System.out.println("Press enter to quit.");

        final Scanner scanner = new Scanner(System.in);
        scanner.hasNextLine();

        mqttClient.close();
    }
}
