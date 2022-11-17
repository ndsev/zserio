<#macro service_arg_type_name typeInfo>
    <#if typeInfo.isBytes>
        ::zserio::Span<const uint8_t><#t>
    <#else>
        const ${typeInfo.typeFullName}&<#t>
    </#if>
</#macro>
