<#include "FileHeader.inc.ftl">
<#include "Sql.inc.ftl">
<#include "DocComment.inc.ftl">
<@standard_header generatorDescription, packageName/>

<#if withCodeComments>
/** Class which describes one row in the table ${name?remove_ending("Row")}. */
</#if>
public final class ${name}
{
<#list fields as field>
    <#if withCodeComments>
    /**
     * Gets the value of the field ${field.name}.
        <#if field.docComments??>
     * <p>
     * <b>Description:</b>
     * <br>
     <@doc_comments_inner field.docComments, 1/>
     *
        <#else>
     *
        </#if>
     * @return Value of the field ${field.name}.
     */
    </#if>
    public ${field.typeInfo.typeFullName} get${field.name?cap_first}()
    {
        return this.<@sql_field_member_name field/>;
    }

    <#if withCodeComments>
    /**
     * Sets the value of the field ${field.name}.
        <#if field.docComments??>
     * <p>
     * <b>Description:</b>
     * <br>
     <@doc_comments_inner field.docComments, 1/>
     *
        <#else>
     *
        </#if>
     * @param <@sql_field_argument_name field/> Value of the field ${field.name} to set.
     */
    </#if>
    public void set${field.name?cap_first}(${field.typeInfo.typeFullName} <@sql_field_argument_name field/>)
    {
        <#if field.nullableTypeInfo.typeFullName != field.typeInfo.typeFullName>
        is${field.name?cap_first}Null = false;
        </#if>
        this.<@sql_field_member_name field/> = <@sql_field_argument_name field/>;
    }

    <#if withCodeComments>
    /**
     * Sets the value of the field ${field.name} to the null value.
     */
    </#if>
    public void setNull${field.name?cap_first}()
    {
        <#if field.nullableTypeInfo.typeFullName != field.typeInfo.typeFullName>
        is${field.name?cap_first}Null = true;
        this.<@sql_field_member_name field/> = <#if field.typeInfo.isBoolean>false<#else>(${field.typeInfo.typeFullName})0</#if>;
        <#else>
        this.<@sql_field_member_name field/> = null;
        </#if>
    }

    <#if withCodeComments>
    /**
     * Checks if the field ${field.name} is set to the null value.
     *
     * @return True if the field ${field.name} is set to the null value, otherwise false.
     */
    </#if>
    public boolean isNull${field.name?cap_first}()
    {
        <#if field.nullableTypeInfo.typeFullName != field.typeInfo.typeFullName>
        return is${field.name?cap_first}Null;
        <#else>
        return this.<@sql_field_member_name field/> == null;
        </#if>
    }

</#list>
<#list fields as field>
    <#if field.nullableTypeInfo.typeFullName != field.typeInfo.typeFullName>
    private boolean is${field.name?cap_first}Null = true;
    </#if>
    private ${field.typeInfo.typeFullName} <@sql_field_member_name field/>;
</#list>
}
