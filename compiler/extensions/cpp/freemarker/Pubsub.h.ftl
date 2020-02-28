<#include "FileHeader.inc.ftl">
<@file_header generatorDescription/>

<@include_guard_begin package.path, name/>

#include <string>
#include <vector>
#include <map>
#include <functional>
#include "zserio/IPubsub.h"
#include "zserio/PubsubException.h"
<@user_includes headerUserIncludes/>
<@namespace_begin package.path/>

class ${name}
{
public:
    explicit ${name}(::zserio::IPubsub& pubsub);
    ~${name}() = default;

    ${name}(const ${name}&) = default;
    ${name}& operator=(const ${name}&) = default;

    ${name}(${name}&&) = default;
    ${name}& operator=(${name}&&) = default;
<#list messageList as message>
    <#if message.isPublished>

    void publish${message.name?cap_first}(${message.typeFullName}& message, void* context = nullptr);
    </#if>
    <#if message.isSubscribed>

    ::zserio::IPubsub::SubscriptionId subscribe${message.name?cap_first}(
            const ::std::function<void(const ::std::string& topic,
                    const ${message.typeFullName}& message)>& callback,
            void* context = nullptr);
    </#if>
</#list>
<#if hasSubscriptions>

    void unsubscribe(::zserio::IPubsub::SubscriptionId id);
</#if>

private:
<#if hasSubscriptions>
    <#list messageList as message>
        <#if message.isSubscribed>
    void onRaw${message.name?cap_first}(::zserio::IPubsub::SubscriptionId id, const ::std::string& topic,
            const ::std::vector<uint8_t>& data);
        </#if>
    </#list>

</#if>
<#if hasPublifications>
    template <typename ZSERIO_MESSAGE>
    void publish(ZSERIO_MESSAGE& message, const ::std::string& topic, void* context);

</#if>
    ::zserio::IPubsub& m_pubsub;
<#if hasSubscriptions>

    <#list messageList as message>
        <#if message.isSubscribed>
    ::std::map<::zserio::IPubsub::SubscriptionId, ::std::function<void(const ::std::string& topic,
            const ${message.typeFullName}& message)>> m_subscribers${message.name?cap_first};
        </#if>
    </#list>
</#if>
};
<@namespace_end package.path/>

<@include_guard_end package.path, name/>
