<#--
Macro for outputting a C++ initializer list.

The constructor prototype must end by '<#rt>' and each member item must be on its own line without comma.

Example:

Constructor::Constructor()<#rt>
    <@cpp_initializer_list>
        m_one(1)
        <#if isTwoPresent>
        m_two(...)
        </#if>
        m_three
    </@cpp_initializer_list>
-->
<#macro cpp_initializer_list>
    <#local body><#nested></#local>
    <#if body?trim != ''>
        <#local members = body?split(r'\R', 'r')>
        <#list members>
            <#lt> :
            <#items as member>
                <#lt>${member}<#sep>,</#sep>
            </#items>
        </#list>
    <#else>

    </#if>
</#macro>
