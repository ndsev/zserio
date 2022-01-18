<#include "FileHeader.inc.ftl">
<#include "Sql.inc.ftl">
<@standard_header generatorDescription, packageName/>

public class ${name}
{
    <#list fields as field>
    public ${field.typeInfo.typeName} get${field.name?cap_first}()
    {
        return this.<@sql_field_member_name field/>;
    }

    public void set${field.name?cap_first}(${field.typeInfo.typeName} <@sql_field_argument_name field/>)
    {
        <#if field.nullableTypeInfo.typeName != field.typeInfo.typeName>
        is${field.name?cap_first}Null = false;
        </#if>
        this.<@sql_field_member_name field/> = <@sql_field_argument_name field/>;
    }

    public void setNull${field.name?cap_first}()
    {
        <#if field.nullableTypeInfo.typeName != field.typeInfo.typeName>
        is${field.name?cap_first}Null = true;
        this.<@sql_field_member_name field/> = <#if field.typeInfo.isBoolean>false<#else>(${field.typeInfo.typeName})0</#if>;
        <#else>
        this.<@sql_field_member_name field/> = null;
        </#if>
    }

    public boolean isNull${field.name?cap_first}()
    {
        <#if field.nullableTypeInfo.typeName != field.typeInfo.typeName>
        return is${field.name?cap_first}Null;
        <#else>
        return this.<@sql_field_member_name field/> == null;
        </#if>
    }

    </#list>
    <#list fields as field>
        <#if field.nullableTypeInfo.typeName != field.typeInfo.typeName>
    private boolean is${field.name?cap_first}Null = true;
        </#if>
    private ${field.typeInfo.typeName} <@sql_field_member_name field/>;
    </#list>
}
