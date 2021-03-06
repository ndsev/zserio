<#include "FileHeader.inc.ftl">
<@file_header generatorDescription/>

#include <zserio/BitStreamReader.h>
#include <zserio/BitStreamWriter.h>

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
    explicit ${name}OnRaw(const std::shared_ptr<${name}::${name}Callback<ZSERIO_MESSAGE>>& callback,
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
    std::shared_ptr<${name}::${name}Callback<ZSERIO_MESSAGE>> m_callback;
    ${types.allocator.default} m_allocator;
};

} // namespace
</#if>

${name}::${name}(::zserio::IPubsub& pubsub, const ${types.allocator.default}& allocator) :
        ::zserio::AllocatorHolder<${types.allocator.default}>(allocator),
        m_pubsub(pubsub)
{
}
<#list messageList as message>
    <#if message.isPublished>

void ${name}::publish${message.name?cap_first}(${message.typeFullName}& message, void* context)
{
    publish(message, ::zserio::makeStringView(${message.topicDefinition}), context);
}
    </#if>
    <#if message.isSubscribed>

::zserio::IPubsub::SubscriptionId ${name}::subscribe${message.name?cap_first}(
        const std::shared_ptr<${name}Callback<${message.typeFullName}>>& callback,
        void* context)
{
    const auto& onRawCallback = ::std::allocate_shared<${name}OnRaw<${message.typeFullName}>>(
            get_allocator_ref(), callback, get_allocator_ref());
    return m_pubsub.subscribe(::zserio::makeStringView(${message.topicDefinition}), onRawCallback, context);
}
    </#if>
</#list>
<#if hasSubscribing>

void ${name}::unsubscribe(::zserio::IPubsub::SubscriptionId id)
{
    m_pubsub.unsubscribe(id);
}
</#if>
<#if hasPublishing>

template <typename ZSERIO_MESSAGE>
void ${name}::publish(ZSERIO_MESSAGE& message, ::zserio::StringView topic, void* context)
{
    <@vector_type_name "uint8_t"/> data((message.bitSizeOf() + 7) / 8, 0, get_allocator_ref());
    ::zserio::BitStreamWriter writer(data.data(), data.size());
    message.write(writer);
    m_pubsub.publish(topic, data, context);
}
</#if>
<@namespace_end package.path/>
