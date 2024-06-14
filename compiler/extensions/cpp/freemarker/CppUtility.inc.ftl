<#-- Macro for outputting a C++ initializer list.
     Each body item must be on its own line.

     Example:
        <@cpp_initializer_list>
            m_one(1)
            m_two(...)
        </@cpp_initializer_list>
  -->
<#macro cpp_initializer_list>
    <#local body><#nested></#local>
    <#if body?trim != ''>
        <#local members = body?split(r'\R', 'r')>
        <#local indent = '    '>
        <#list members>
            <#lt> :
            <#items as member>
                <#local member = member?trim>
                <#if member == ''>
                    <#continue>
                </#if>
                <#lt>${indent}${member?remove_ending(',')}<#rt>
                <#sep><#lt>,
                </#sep>
            </#items>
            <#t>${'\n'}
        </#list>
    <#else>
        ${'\n'}<#t>
    </#if>
</#macro>
