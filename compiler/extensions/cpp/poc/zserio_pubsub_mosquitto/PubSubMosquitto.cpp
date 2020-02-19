#include "PubSubMosquitto.h"

namespace zserio_pubsub_mosquitto
{

extern "C" void message_callback(struct mosquitto* mosq, void* userdata, const struct mosquitto_message* msg)
{
    static_cast<MosquittoSubscription*>(userdata)->callback(mosq, msg);
}

MosquittoSubscription::MosquittoSubscription(const std::string& host, uint16_t port,
        zserio::IPubSubClient::SubscriptionId id, const std::string& topic,
        const zserio::IPubSubClient::OnTopic& callback)
:   m_host(host), m_port(port), m_id(id), m_topic(topic), m_callback(callback)
{}

MosquittoSubscription::~MosquittoSubscription()
{
    if (m_mosq)
    {
        mosquitto_unsubscribe(m_mosq.get(), nullptr, m_topic.c_str());
        mosquitto_disconnect(m_mosq.get()); // TODO: handle status?
        mosquitto_loop_stop(m_mosq.get(), false);
    }
}

MosquittoSubscription::MosquittoSubscription(MosquittoSubscription&& other)
:   m_host(std::move(other.m_host)), m_port(std::move(other.m_port)), m_id(std::move(other.m_id)),
    m_topic(std::move(other.m_topic)), m_callback(std::move(other.m_callback))
{
    m_mosq.swap(other.m_mosq);
}

void MosquittoSubscription::init()
{
    m_mosq.reset(mosquitto_new(nullptr, true, this));
    if (!m_mosq)
        throw std::runtime_error("Failed to create mosquitto instance!");
    mosquitto_message_callback_set(m_mosq.get(), message_callback);
    int rc = mosquitto_connect(m_mosq.get(), m_host.c_str(), m_port, 60);
    if (rc)
    {
        throw std::runtime_error(std::string("MosquittoSubscription " + std::to_string(m_id) +
                " failed to connect! ") + mosquitto_strerror(rc));
    }
    mosquitto_subscribe(m_mosq.get(), nullptr, m_topic.c_str(), 0);
    mosquitto_loop_start(m_mosq.get());
}

void MosquittoSubscription::callback(struct mosquitto* mosq, const struct mosquitto_message* msg)
{
    uint8_t* payload = static_cast<uint8_t*>(msg->payload);
    uint32_t payloadlen = msg->payloadlen;
    std::vector<uint8_t> data(payload, payload + payloadlen);
    m_callback(msg->topic, data);
}

MosquittoClient::MosquittoClient(const std::string& host, uint16_t port)
:   m_host(host), m_port(port), m_numIds(0)
{}

void MosquittoClient::publish(const std::string& topic, const std::vector<uint8_t>& data, void*)
{
    // TODO: use the context
    MosquittoPtr mosq(mosquitto_new(nullptr, true, this));
    int rc = mosquitto_connect(mosq.get(), m_host.c_str(), m_port, 60);
    if (rc)
    {
        throw std::runtime_error(std::string("MosquittoClient failed to connect! ") +
                mosquitto_strerror(rc));
    }
    mosquitto_publish(mosq.get(), nullptr, topic.c_str(), data.size(), data.data(), 0, 0);
    mosquitto_disconnect(mosq.get());
}

MosquittoClient::SubscriptionId MosquittoClient::subscribe(const std::string& topic,
        const OnTopic& callback, void*)
{
    // TODO: use the context
    auto inserted = m_subscriptions.emplace(m_numIds,
            MosquittoSubscription{m_host, m_port, m_numIds, topic, callback});
    inserted.first->second.init();
    return m_numIds++;
}

void MosquittoClient::unsubscribe(SubscriptionId id)
{
    m_subscriptions.erase(id);
}

} // namespace zserio_pubsub_mosquitto
