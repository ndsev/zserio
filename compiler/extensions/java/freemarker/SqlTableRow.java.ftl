<#include "FileHeader.inc.ftl">
<#include "CompoundField.inc.ftl">
<@standard_header generatorDescription, packageName/>

public class ${name}
{
    <#list fields as field>
    public ${field.javaTypeFullName} get${field.name?cap_first}()
    {
        return this.<@field_member_name field/>;
    }

    public void set${field.name?cap_first}(${field.javaTypeFullName} <@field_argument_name field/>)
    {
        <#if field.javaNullableTypeFullName != field.javaTypeFullName>
        is${field.name?cap_first}Null = false;
        </#if>
        this.<@field_member_name field/> = <@field_argument_name field/>;
    }

    public void setNull${field.name?cap_first}()
    {
        <#if field.javaNullableTypeFullName != field.javaTypeFullName>
        is${field.name?cap_first}Null = true;
        this.<@field_member_name field/> = <#if field.isBool>false<#else>(${field.javaTypeFullName})0</#if>;
        <#else>
        this.<@field_member_name field/> = null;
        </#if>
    }

    public boolean isNull${field.name?cap_first}()
    {
        <#if field.javaNullableTypeFullName != field.javaTypeFullName>
        return is${field.name?cap_first}Null;
        <#else>
        return this.<@field_member_name field/> == null;
        </#if>
    }

    </#list>
    <#list fields as field>
        <#if field.javaNullableTypeFullName != field.javaTypeFullName>
    private boolean is${field.name?cap_first}Null = true;
        </#if>
    private ${field.javaTypeFullName} <@field_member_name field/>;
    </#list>
}
