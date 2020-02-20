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
            std::cout << "Calculating power of two for: " << value.getValue() << std::endl;

            const uint64_t absValue = value.getValue() > 0
                    ? static_cast<uint64_t>(value.getValue())
                    : static_cast<uint64_t>(-value.getValue());

            pubsub_poc::UInt64Value uint64Value{absValue * absValue};
            simplePubSub.publishUint64ValuePub(uint64Value);
        }
    );

    std::cout << "Power of two calculator, waiting for pubsub/int32..." << std::endl;
    std::cout << "Press enter to quit." << std::endl;

    std::string input;
    getline(std::cin, input);

    return 0;
}
