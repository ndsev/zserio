#include <string>
#include <iostream>

#include "zserio_runtime/IPubSubClient.h"
#include "pubsub_poc/PositivenessProvider.h"
#include "zserio_pubsub_mosquitto/PubSubMosquitto.h"

int main(int argc, char* argv[])
{
    std::string host = "localhost";
    uint16_t port = 1234;

    zserio_pubsub_mosquitto::MosquittoInitializer mosquittoInitializer;

    zserio_pubsub_mosquitto::MosquittoClient mosquittoClient(host, port);
    pubsub_poc::PositivenessProvider positivenessProvider(mosquittoClient);

    positivenessProvider.subscribeRequest(
        [&positivenessProvider](const std::string& topic, const pubsub_poc::Int32Value& value)
        {
            std::cout << "Checking positiveness of the value: " << value.getValue()  << std::endl;

            pubsub_poc::BoolValue boolValue{value.getValue() >= 0};
            positivenessProvider.publishPositiveness(boolValue);
        }
    );

    std::cout << "Positiveness checker,  waiting for pubsub/request..." << std::endl;
    std::cout << "Press enter to quit." << std::endl;

    std::string input;
    getline(std::cin, input);

    return 0;
}
