<#function sql_table_has_blob_field fields>
    <#list fields as field>
        <#if field.sqlTypeData.isBlob>
            <#return true>
        </#if>
    </#list>
    <#return false>
</#function>

<#function sql_table_has_non_virtual_field fields>
    <#list fields as field>
        <#if !field.isVirtual>
            <#return true>
        </#if>
    </#list>
    <#return false>
</#function>

<#function sql_table_has_validatable_field fields>
    <#list fields as field>
        <#if !field.isVirtual>
            <#if field.sqlTypeData.isBlob || field.enumData?? || field.rangeCheckData.sqlRangeData??>
                <#return true>
            </#if>
        </#if>
    </#list>
    <#return false>
</#function>

<#function sql_db_needs_parameter_provider fields>
    <#list fields as field>
        <#if field.hasExplicitParameters>
            <#return true>
        </#if>
    </#list>
    <#return false>
</#function>

<#function sql_strip_quotes string>
    <#return string[1..string?length - 2]>
</#function>

<#macro sql_parameter_provider_getter_name parameter>
    get${parameter.expression?cap_first}<#t>
</#macro>
