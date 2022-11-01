<#function sql_table_has_non_virtual_field fields>
    <#list fields as field>
        <#if !field.isVirtual>
            <#return true>
        </#if>
    </#list>
    <#return false>
</#function>

<#function sql_db_has_without_rowid_table fields>
    <#list fields as field>
        <#if field.isWithoutRowIdTable>
            <#return true>
        </#if>
    </#list>
    <#return false>
</#function>

<#macro sql_db_table_name_getter field>
    tableName${field.name?cap_first}()<#t>
</#macro>

<#macro sql_field_member_name field>
    m_${field.name}_<#t>
</#macro>

<#macro sql_field_argument_name field>
    ${field.name}_<#t>
</#macro>

<#macro sql_parameter_provider_getter_name parameter>
    get${parameter.expression?cap_first}<#t>
</#macro>

<#macro sql_parameter_provider_return_type parameter>
    <#if parameter.typeInfo.isSimple>
        ${parameter.typeInfo.typeFullName}<#t>
    <#else>
        <#-- non-const reference is necessary for setting of offsets -->
        ${parameter.typeInfo.typeFullName}&<#t>
    </#if>
</#macro>

<#macro sql_parameter_variable_type parameter>
    <#if parameter.typeInfo.isSimple>
        const ${parameter.typeInfo.typeFullName}<#t>
    <#else>
        ${parameter.typeInfo.typeFullName}&<#t>
    </#if>
</#macro>

<#macro sql_db_table_parameter_provider_getter field>
    get${field.name?cap_first}ParameterProvider()<#t>
</#macro>

<#function sql_db_needs_parameter_provider fields>
    <#list fields as field>
        <#if field.hasExplicitParameters>
            <#return true>
        </#if>
    </#list>
    <#return false>
</#function>
