#include <string>
#include <iostream>

#include "zserio_runtime/IPubSubClient.h"
#include "pubsub_poc/GreaterThanTenProvider.h"
#include "zserio_pubsub_mosquitto/PubSubMosquitto.h"

int main(int argc, char* argv[])
{
    std::string host = "localhost";
    uint16_t port = 1234;

    zserio_pubsub_mosquitto::MosquittoInitializer mosquittoInitializer;

    zserio_pubsub_mosquitto::MosquittoClient mosquittoClient(host, port);
    pubsub_poc::GreaterThanTenProvider greaterThanTenProvider(mosquittoClient);

    greaterThanTenProvider.subscribeRequest(
        [&greaterThanTenProvider](const std::string& topic, const pubsub_poc::Int32Value& value)
        {
            std::cout << "Checking if the value is greater than 10: " << value.getValue()  << std::endl;

            pubsub_poc::BoolValue boolValue{value.getValue() > 10};
            greaterThanTenProvider.publishGreaterThanTen(boolValue);
        }
    );

    std::cout << "Greater than 10 checker,  waiting for pubsub/request..." << std::endl;
    std::cout << "Press enter to quit." << std::endl;

    std::string input;
    getline(std::cin, input);

    return 0;
}
