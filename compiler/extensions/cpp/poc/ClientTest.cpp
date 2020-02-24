#include <string>
#include <iostream>
#include <mutex>

#include "zserio_runtime/IPubSubClient.h"
#include "pubsub_poc/Client.h"
#include "zserio_pubsub_mosquitto/PubSubMosquitto.h"

int main(int argc, char* argv[])
{
    std::string host = "localhost";
    uint16_t port = 1234;

    zserio_pubsub_mosquitto::MosquittoInitializer mosquittoInitializer;

    zserio_pubsub_mosquitto::MosquittoClient mosquittoClient(host, port);
    pubsub_poc::Client client(mosquittoClient);

    auto powerOfTwoCallback = [](const std::string& topic, const pubsub_poc::UInt64Value& value)
    {
        std::cout << "power of two: " << value.getValue() << std::endl;
    };
    zserio::IPubSubClient::SubscriptionId powerOfTwoId = client.subscribePowerOfTwo(powerOfTwoCallback);
    bool powerOfTwoSubscribed = true;

    auto booleanResponseCallback = [](const std::string& topic, const pubsub_poc::BoolValue& value)
    {
        std::cout << topic << ": " << (value.getValue() ? "true" : "false") << std::endl;
    };
    zserio::IPubSubClient::SubscriptionId booleanResponseId =
            client.subscribeBooleanResponse(booleanResponseCallback);
    bool booleanResponseSubscribed = true;

    while (true)
    {
        std::string input;
        getline(std::cin, input);

        if (input.empty())
            continue;

        if (input.at(0) == 'q')
            return 0;

        if (input.at(0) == 'u')
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
            std::cout << "power of two " << (powerOfTwoSubscribed ? "enabled" : "disabled") << std::endl;
            continue;
        }

        if (input.at(0) == 'b')
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
            std::cout << "boolean response "
                      << (booleanResponseSubscribed ? "enabled" : "disabled") << std::endl;
            continue;
        }

        // publish and answers will come
        pubsub_poc::Int32Value int32Value{std::stoi(input)};
        client.publishRequest(int32Value);
    }

    return 0;
}
