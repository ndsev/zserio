<#include "FileHeader.inc.ftl">
<#include "TypeInfo.inc.ftl">
<#include "Pubsub.inc.ftl">
<@file_header generatorDescription/>

#include <zserio/BitStreamReader.h>
#include <zserio/BitStreamWriter.h>
<@type_includes types.bitBuffer/>
<#if withTypeInfoCode>
#include <zserio/TypeInfo.h>

</#if>
<@system_includes cppSystemIncludes/>

<@user_include package.path, "${name}.h"/>
<@user_includes cppUserIncludes, false/>
<@namespace_begin package.path/>
<#if hasSubscribing>

namespace
{

template <typename ZSERIO_MESSAGE>
class ${name}OnRaw : public ::zserio::IPubsub::OnTopicCallback
{
public:
    explicit ${name}OnRaw(const ::std::shared_ptr<${name}::${name}Callback<ZSERIO_MESSAGE>>& callback,
            const ${types.allocator.default}& allocator) :
            m_callback(callback), m_allocator(allocator)
    {}

    void operator()(::zserio::StringView topic, ::zserio::Span<const uint8_t> data) override
    {
        ::zserio::BitStreamReader reader(data.data(), data.size());
        const ZSERIO_MESSAGE message(reader, m_allocator);

        m_callback->operator()(topic, message);
    }

private:
    ::std::shared_ptr<${name}::${name}Callback<ZSERIO_MESSAGE>> m_callback;
    ${types.allocator.default} m_allocator;
};
<#if has_subscribed_bytes(messageList)>

// specialization for bytes
template <>
void ${name}OnRaw<::zserio::Span<const uint8_t>>::operator()(::zserio::StringView topic,
        ::zserio::Span<const uint8_t> data)
{
    m_callback->operator()(topic, data);
}
</#if>

} // namespace
</#if>

${name}::${name}(::zserio::IPubsub& pubsub, const allocator_type& allocator) :
        ::zserio::AllocatorHolder<allocator_type>(allocator),
        m_pubsub(pubsub)
{
}
<#if withTypeInfoCode>

const ${types.typeInfo.name}& ${name}::typeInfo()
{
    static const <@info_array_type "::zserio::BasicMessageInfo<allocator_type>", messageList?size/> messages<#rt>
    <#if messageList?has_content>
        <#lt> = {
        <#list messageList as message>
        <@message_info message message?has_next/>
        </#list>
    };
    <#else>
        <#lt>;
    </#if>

    static const ::zserio::PubsubTypeInfo<allocator_type> typeInfo = {
        ::zserio::makeStringView("${schemaTypeName}"), messages
    };

    return typeInfo;
}
</#if>
<#list messageList as message>
    <#if message.isPublished>

void ${name}::publish${message.name?cap_first}(<@pubsub_arg_type_name message.typeInfo/> message, void* context)
{
        <#if message.typeInfo.isBytes>
    m_pubsub.publish(${message.topicDefinition}, message, context);
        <#else>
    publish(message, ${message.topicDefinition}, context);
        </#if>
}
    </#if>
    <#if message.isSubscribed>

::zserio::IPubsub::SubscriptionId ${name}::subscribe${message.name?cap_first}(
        const ::std::shared_ptr<${name}Callback<<@pubsub_type_name message.typeInfo/>>>& callback,
        void* context)
{
    const auto& onRawCallback = ::std::allocate_shared<${name}OnRaw<<@pubsub_type_name message.typeInfo/>>>(
            get_allocator_ref(), callback, get_allocator_ref());
    return m_pubsub.subscribe(${message.topicDefinition}, onRawCallback, context);
}
    </#if>
</#list>
<#if hasSubscribing>

void ${name}::unsubscribe(::zserio::IPubsub::SubscriptionId id)
{
    m_pubsub.unsubscribe(id);
}
</#if>
<#if hasPublishing && has_published_object(messageList)>

template <typename ZSERIO_MESSAGE>
void ${name}::publish(ZSERIO_MESSAGE& message, ::zserio::StringView topic, void* context)
{
    ${types.bitBuffer.name} bitBuffer(message.bitSizeOf(), get_allocator_ref());
    ::zserio::BitStreamWriter writer(bitBuffer);
    message.write(writer);
    m_pubsub.publish(topic, bitBuffer.getBytes(), context);
}
</#if>
<@namespace_end package.path/>
