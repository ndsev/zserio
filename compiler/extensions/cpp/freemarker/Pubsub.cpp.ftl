<#include "FileHeader.inc.ftl">
<@file_header generatorDescription/>

#include "zserio/BitStreamReader.h"
#include "zserio/BitStreamWriter.h"
<@user_include package.path, "${name}.h"/>
<@user_includes cppUserIncludes, false/>
<@namespace_begin package.path/>

${name}::${name}(::zserio::IPubsub& pubsub) : m_pubsub(pubsub)
{
}
<#list messageList as message>
    <#if message.isPublished>

void ${name}::publish${message.name?cap_first}(${message.typeFullName}& message, void* context)
{
    publish(message, "${message.topicDefinition}", context);
}
    </#if>
    <#if message.isSubscribed>

::zserio::IPubsub::SubscriptionId ${name}::subscribe${message.name?cap_first}(
        const ::std::function<void(const ::std::string&, const ${message.typeFullName}&)>& callback,
        void* context)
{
    const ::zserio::IPubsub::SubscriptionId id = m_pubsub.reserveId();
    const auto result = m_subscribers${message.name?cap_first}.emplace(id, callback);
    if (!result.second)
    {
        throw ::zserio::PubsubException(std::string("${fullName}: ") +
                "Subscription ID '" + std::to_string(id) + "' already in use!");
    }

    const ::std::string topic = "${message.topicDefinition}";
    const ::zserio::IPubsub::OnTopic rawCallback = ::std::bind(&${name}::onRaw${message.name?cap_first}, this,
            ::std::placeholders::_1, ::std::placeholders::_2, ::std::placeholders::_3);
    m_pubsub.subscribe(id, topic, rawCallback, context);

    return id;
}
    </#if>
</#list>
<#if hasSubscriptions>

void ${name}::unsubscribe(::zserio::IPubsub::SubscriptionId id)
{
    <#list messageList as message>
        <#if message.isSubscribed>
    const auto found${message.name?cap_first} = m_subscribers${message.name?cap_first}.find(id);
    if (found${message.name?cap_first} != m_subscribers${message.name?cap_first}.end())
    {
        m_subscribers${message.name?cap_first}.erase(found${message.name?cap_first});
        return;
    }
        </#if>
    </#list>
}
    <#list messageList as message>
        <#if message.isSubscribed>

void ${name}::onRaw${message.name?cap_first}(::zserio::IPubsub::SubscriptionId id, const ::std::string& topic,
        const ::std::vector<uint8_t>& data)
{
    ::zserio::BitStreamReader reader(data.data(), data.size());
    const ${message.typeFullName} message(reader);

    const auto found = m_subscribers${message.name?cap_first}.find(id);
    if (found == m_subscribers${message.name?cap_first}.end())
    {
        throw ::zserio::PubsubException(std::string("${fullName}: ") +
                "Unknown subscription ID '" + std::to_string(id) + "' for '${message.name}' message");
    }

    found->second(topic, message);
}
        </#if>
     </#list>
</#if>
<#if hasPublifications>
template <typename ZSERIO_MESSAGE>
void ${name}::publish(ZSERIO_MESSAGE& message, const ::std::string& topic, void* context)
{
    ::std::vector<uint8_t> data((message.bitSizeOf() + 7) / 8);
    ::zserio::BitStreamWriter writer(data.data(), data.size());
    message.write(writer);
    m_pubsub.publish(topic, data, context);
}
</#if>
<@namespace_end package.path/>
