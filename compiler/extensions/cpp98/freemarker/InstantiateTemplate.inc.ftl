<#-- Output C++ template instantiation while avoiding the A<B<C>> problem. -->
<#macro instantiate_template name args...>
    <#lt>${name}<<#rt>
    <#local addSpace = false>
    <#list args as arg>
        <#lt>${arg}<#if arg_has_next>, </#if><#rt>
        <#local addSpace = arg?ends_with(">")>
    </#list>
    <#lt><#if addSpace> </#if>><#rt>
</#macro>
