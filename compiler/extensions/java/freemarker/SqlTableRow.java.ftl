<#include "FileHeader.inc.ftl">
<#include "GeneratePkgPrefix.inc.ftl">
<@standard_header generatorDescription, packageName, []/>

public class ${name}
{
    <#list fields as field>
    public ${field.javaTypeFullName} get${field.name?cap_first}()
    {
        return ${field.name};
    }

    public void set${field.name?cap_first}(${field.javaTypeFullName} ${field.name})
    {
        <#if field.javaNullableTypeFullName != field.javaTypeFullName>
        is${field.name?cap_first}Null = false;
        </#if>
        this.${field.name} = ${field.name};
    }

    public void setNull${field.name?cap_first}()
    {
        <#if field.javaNullableTypeFullName != field.javaTypeFullName>
        is${field.name?cap_first}Null = true;
        ${field.name} = <#if field.isBool>false<#else>(${field.javaTypeFullName})0</#if>;
        <#else>
        ${field.name} = null;
        </#if>
    }

    public boolean isNull${field.name?cap_first}()
    {
        <#if field.javaNullableTypeFullName != field.javaTypeFullName>
        return is${field.name?cap_first}Null;
        <#else>
        return ${field.name} == null;
        </#if>
    }

    </#list>
    <#list fields as field>
        <#if field.javaNullableTypeFullName != field.javaTypeFullName>
    private boolean is${field.name?cap_first}Null = true;
        </#if>
    private ${field.javaTypeFullName} ${field.name};
    </#list>
}
