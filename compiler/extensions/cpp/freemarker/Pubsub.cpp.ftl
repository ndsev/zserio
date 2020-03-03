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
    const ::zserio::IPubsub::OnTopic onRaw = ::std::bind(&${name}::onRaw${message.name?cap_first}, this,
            callback, ::std::placeholders::_1, ::std::placeholders::_2);
    return m_pubsub.subscribe("${message.topicDefinition}", onRaw, context);
}
    </#if>
</#list>
<#if hasSubscriptions>

void ${name}::unsubscribe(::zserio::IPubsub::SubscriptionId id)
{
    m_pubsub.unsubscribe(id);
}
    <#list messageList as message>
        <#if message.isSubscribed>

void ${name}::onRaw${message.name?cap_first}(
        const ::std::function<void(const ::std::string&, const ${message.typeFullName}&)>& callback,
        const ::std::string& topic, const ::std::vector<uint8_t>& data)
{
    ::zserio::BitStreamReader reader(data.data(), data.size());
    const ${message.typeFullName} message(reader);

    callback(topic, message);
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
