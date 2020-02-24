#ifndef ZSERIO_IPUB_SUB_CLIENT_H_INC
#define ZSERIO_IPUB_SUB_CLIENT_H_INC

#include <string>
#include <vector>
#include <functional>

namespace zserio
{

class IPubSubClient
{
public:
    using SubscriptionId = size_t;
    using OnTopic =
            std::function<void(SubscriptionId id, const std::string& topic, const std::vector<uint8_t>& data)>;

    virtual ~IPubSubClient() = default;

    virtual void publish(const std::string& topic, const std::vector<uint8_t>& data, void* context) = 0;
    virtual SubscriptionId subscribe(const std::string& topic, const OnTopic& callback, void* context) = 0;
    virtual void unsubscribe(SubscriptionId id) = 0;
};

} // namespace zserio

#endif // ifndef ZSERIO_IPUB_SUB_CLIENT_H_INC
