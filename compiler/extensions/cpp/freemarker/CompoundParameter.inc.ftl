<#include "DocComment.inc.ftl">
<#macro parameter_argument_name paramName>
    ${paramName}_<#t>
</#macro>

<#macro compound_parameter_arguments_type_list compoundParametersData, indent>
    <#local I>${""?left_pad(indent * 4)}</#local>
    <#list compoundParametersData.list as compoundParameter>
    <#local parameterType>
        <#if !compoundParameter.typeInfo.isSimple>
            <#if withWriterCode>
                ${compoundParameter.typeInfo.typeFullName}&<#t>
            <#else>
                const ${compoundParameter.typeInfo.typeFullName}&<#t>
            </#if>
        <#else>
            ${compoundParameter.typeInfo.typeFullName}<#t>
        </#if>
    </#local>
${I}${parameterType} <@parameter_argument_name compoundParameter.name/><#rt>
        <#if compoundParameter?has_next>
            <#lt>,
        </#if>
    </#list>
</#macro>

<#function has_non_simple_parameter compoundParametersData>
    <#list compoundParametersData.list as compoundParameter>
        <#if !compoundParameter.typeInfo.isSimple>
            <#return true>
        </#if>
    </#list>
    <#return false>
</#function>
