#ifndef ZSERIO_PUBSUB_MOSQUITTO_PUB_SUB_MOSQUITTO_H
#define ZSERIO_PUBSUB_MOSQUITTO_PUB_SUB_MOSQUITTO_H

#include <map>
#include <memory>
#include <mosquitto.h>

#include "zserio_runtime/IPubSubClient.h"

namespace zserio_pubsub_mosquitto
{

extern "C" void message_callback(struct mosquitto* mosq, void* userdata, const struct mosquitto_message* msg);

struct MosquittoDestroyer
{
    void operator()(struct mosquitto* mosq) const
    {
        mosquitto_destroy(mosq);
    }
};

typedef std::unique_ptr<struct mosquitto, MosquittoDestroyer> MosquittoPtr;

class MosquittoSubscription
{
public:
    MosquittoSubscription(const std::string& host, uint16_t port,
            zserio::IPubSubClient::SubscriptionId id, const std::string& topic,
            const zserio::IPubSubClient::OnTopic& callback);

    ~MosquittoSubscription();

    MosquittoSubscription(const MosquittoSubscription& other) = delete;
    MosquittoSubscription& operator=(const MosquittoSubscription& other) = delete;

    MosquittoSubscription(MosquittoSubscription&& other);
    MosquittoSubscription& operator=(MosquittoSubscription&& other) = delete;

    void init();

    void callback(struct mosquitto* mosq, const struct mosquitto_message* msg);

private:
    std::string m_host;
    uint16_t m_port;
    zserio::IPubSubClient::SubscriptionId m_id;
    std::string m_topic;
    zserio::IPubSubClient::OnTopic m_callback;

    MosquittoPtr m_mosq;
};

class MosquittoClient : public zserio::IPubSubClient
{
public:
    MosquittoClient(const std::string& host, uint16_t port);

    virtual void publish(const std::string& topic, const std::vector<uint8_t>& data, void* context) override;
    virtual SubscriptionId subscribe(const std::string& topic, const OnTopic& callback, void*) override;
    virtual void unsubscribe(SubscriptionId id) override;

private:
    std::string m_host;
    uint16_t m_port;
    std::map<SubscriptionId, MosquittoSubscription> m_subscriptions;
    SubscriptionId m_numIds;
};

struct MosquittoInitializer
{
    MosquittoInitializer() { mosquitto_lib_init(); }
    ~MosquittoInitializer() { mosquitto_lib_cleanup(); }
};

} // namespace zserio_pubsub_mosquitto

#endif //ZSERIO_PUBSUB_MOSQUITTO_PUB_SUB_MOSQUITTO_H
