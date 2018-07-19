<#macro inspector_zserio_type_name cppTypeName>
    ${cppTypeName?replace(".", "_")?replace("[]", "_array")?replace("()", "_param")}_type_name<#t>
</#macro>

<#macro inspector_zserio_name name>
    ${name}_name<#t>
</#macro>

<#macro inspector_parameter_name parameter>
    <#if parameter.isExplicit>
        ${parameter.expression}<#t>
    <#else>
        ${parameter.fieldName}_${parameter.definitionName}<#t>
    </#if>
</#macro>

<#macro inspector_parameter_provider_name parameter>
    get${parameter.tableName}_<@inspector_parameter_name parameter/><#t>
</#macro>
