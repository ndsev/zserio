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
    publish(message, ${message.topicDefinition}, context);
}
    </#if>
    <#if message.isSubscribed>

::zserio::IPubsub::SubscriptionId ${name}::subscribe${message.name?cap_first}(
        const ::std::function<void(const ::std::string&, const ${message.typeFullName}&)>& callback,
        void* context)
{
    const ::zserio::IPubsub::OnTopic onRawCallback = ::std::bind(
            &${name}::onRaw<${message.typeFullName}>,
            this, callback, ::std::placeholders::_1, ::std::placeholders::_2);
    return m_pubsub.subscribe(${message.topicDefinition}, onRawCallback, context);
}
    </#if>
</#list>
<#if hasSubscribing>

void ${name}::unsubscribe(::zserio::IPubsub::SubscriptionId id)
{
    m_pubsub.unsubscribe(id);
}

template <typename ZSERIO_MESSAGE>
void ${name}::onRaw(
        const ::std::function<void(const ::std::string&, const ZSERIO_MESSAGE&)>& callback,
        const ::std::string& topic, const ::std::vector<uint8_t>& data)
{
    ::zserio::BitStreamReader reader(data.data(), data.size());
    const ZSERIO_MESSAGE message(reader);

    callback(topic, message);
}
</#if>
<#if hasPublishing>

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
