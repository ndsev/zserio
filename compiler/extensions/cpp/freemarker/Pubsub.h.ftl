<#include "FileHeader.inc.ftl">
<@file_header generatorDescription/>

<@include_guard_begin package.path, name/>

#include <memory>
#include <zserio/AllocatorHolder.h>
#include <zserio/IPubsub.h>
#include <zserio/PubsubException.h>
<@type_includes types.vector/>
<@user_includes headerUserIncludes/>
<@namespace_begin package.path/>

class ${name} : public zserio::AllocatorHolder<${types.allocator.default}>
{
public:
    explicit ${name}(::zserio::IPubsub& pubsub, const allocator_type& allocator = allocator_type());
    ~${name}() = default;

    ${name}(const ${name}&) = delete;
    ${name}& operator=(const ${name}&) = delete;

    ${name}(${name}&&) = default;
    ${name}& operator=(${name}&&) = delete;
<#if hasSubscribing>

    template <typename ZSERIO_MESSAGE>
    class ${name}Callback
    {
    public:
        virtual ~${name}Callback() = default;
        virtual void operator()(::zserio::StringView topic, const ZSERIO_MESSAGE& message) = 0;
    };
</#if>
<#list messageList as message>
    <#if message.isPublished>

    void publish${message.name?cap_first}(${message.typeFullName}& message, void* context = nullptr);
    </#if>
    <#if message.isSubscribed>

    ::zserio::IPubsub::SubscriptionId subscribe${message.name?cap_first}(
            const std::shared_ptr<${name}Callback<${message.typeFullName}>>& callback,
            void* context = nullptr);
    </#if>
</#list>
<#if hasSubscribing>

    void unsubscribe(::zserio::IPubsub::SubscriptionId id);
</#if>

private:
<#if hasPublishing>
    template <typename ZSERIO_MESSAGE>
    void publish(ZSERIO_MESSAGE& message, ::zserio::StringView topic, void* context);

</#if>
    ::zserio::IPubsub& m_pubsub;
};
<@namespace_end package.path/>

<@include_guard_end package.path, name/>
