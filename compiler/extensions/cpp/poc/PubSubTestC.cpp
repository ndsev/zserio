#include <string>
#include <iostream>

#include "zserio_runtime/IPubSubClient.h"
#include "pubsub_poc/SimplePubSub.h"
#include "zserio_pubsub_mosquitto/PubSubMosquitto.h"

int main(int argc, char* argv[])
{
    std::string host = "localhost";
    uint16_t port = 1234;

    zserio_pubsub_mosquitto::MosquittoInitializer mosquittoInitializer;

    zserio_pubsub_mosquitto::MosquittoClient mosquittoClient(host, port);
    pubsub_poc::SimplePubSub simplePubSub(mosquittoClient);

    simplePubSub.subscribeInt32ValueSub(
        [&simplePubSub](const std::string& topic, const pubsub_poc::Int32Value& value)
        {
            std::cout << "Checking positiveness of the value: " << value.getValue()  << std::endl;

            pubsub_poc::BoolValue boolValue{value.getValue() >= 0};
            simplePubSub.publishBoolValuePub(boolValue);
        }
    );

    std::cout << "Positiveness checker,  waiting for pubsub/int32..." << std::endl;
    std::cout << "Press enter to quit." << std::endl;

    std::string input;
    getline(std::cin, input);

    return 0;
}
