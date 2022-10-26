<#macro pubsub_type_name typeInfo>
    <#if typeInfo.isBytes>
        ::zserio::Span<const uint8_t><#t>
    <#else>
        ${typeInfo.typeFullName}<#t>
    </#if>
</#macro>

<#macro pubsub_arg_type_name typeInfo>
    <#if typeInfo.isBytes>
        ::zserio::Span<const uint8_t><#t>
    <#else>
        const ${typeInfo.typeFullName}&<#t>
    </#if>
</#macro>

<#function has_subscribed_bytes messageList>
    <#list messageList as message>
        <#if message.isSubscribed && message.typeInfo.isBytes>
            <#return true>
        </#if>
    </#list>
    <#return false>
</#function>

<#function has_published_object messageList>
    <#list messageList as message>
        <#if message.isPublished && !message.typeInfo.isBytes>
            <#return true>
        </#if>
    </#list>
    <#return false>
</#function>
