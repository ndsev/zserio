#include <string>
#include <iostream>
#include <mutex>

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

    auto powerOfTwo = [](const std::string& topic, const pubsub_poc::UInt64Value& value)
    {
        std::cout << "power of two: " << value.getValue() << std::endl;
    };
    zserio::IPubSubClient::SubscriptionId powerOfTwoId = simplePubSub.subscribeUint64ValueSub(powerOfTwo);
    bool powerOfTwoSubscribed = true;

    auto positiveness = [](const std::string& topic, const pubsub_poc::BoolValue& value)
    {
        std::cout << "is positive: " << (value.getValue() ? "true" : "false") << std::endl;
    };
    zserio::IPubSubClient::SubscriptionId positivenessId = simplePubSub.subscribeBoolValueSub(positiveness);
    bool positivenessSubscribed = true;

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
                simplePubSub.unsubscribeUint64ValueSub(powerOfTwoId);
                powerOfTwoSubscribed = false;
            }
            else
            {
                powerOfTwoId = simplePubSub.subscribeUint64ValueSub(powerOfTwo);
                powerOfTwoSubscribed = true;
            }
            std::cout << "power of two " << (powerOfTwoSubscribed ? "enabled" : "disabled") << std::endl;
            continue;
        }

        if (input.at(0) == 'b')
        {
            if (positivenessSubscribed)
            {
                simplePubSub.unsubscribeBoolValueSub(positivenessId);
                positivenessSubscribed = false;
            }
            else
            {
                positivenessId = simplePubSub.subscribeBoolValueSub(positiveness);
                positivenessSubscribed = true;
            }
            std::cout << "positiveness " << (positivenessSubscribed ? "enabled" : "disabled") << std::endl;
            continue;
        }

        // publish and answers will come
        pubsub_poc::Int32Value int32Value{std::stoi(input)};
        simplePubSub.publishInt32ValuePub(int32Value);
    }

    return 0;
}
