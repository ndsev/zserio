<#include "FileHeader.inc.ftl">
<#include "DocComment.inc.ftl">
<#include "CompoundField.inc.ftl">
<#include "CompoundParameter.inc.ftl">
<@standard_header generatorDescription, packageName/>

<#if withCodeComments && docComments??>
<@doc_comments docComments/>
</#if>
public class ${name} extends ${referencedTypeFullName}
{
<#if isReferencedTypeSqlTable>
    <#if withCodeComments>
    /**
     * Constructor from database connection and table name.
     *
     * @param connection Database connection where the table is located.
     * @param tableName Table name.
     */
    </#if>
    public ${name}(java.sql.Connection connection, java.lang.String tableName)
    {
        super(connection, tableName);
    }

    <#if withCodeComments>
    /**
     * Constructor from database connection, table name and attached database name.
     *
     * @param connection Database connection where the table is located.
     * @param attachedDbName Name of the attached database where table has been relocated.
     * @param tableName Table name.
     */
    </#if>
    public ${name}(java.sql.Connection connection, java.lang.String attachedDbName,
            java.lang.String tableName)
    {
        super(connection, attachedDbName, tableName);
    }
<#elseif referencedCompoundType??>
    <#if withWriterCode>
        <#if withCodeComments>
    /**
     * Default constructor.
     *
     <@compound_parameter_comments referencedCompoundType.compoundParametersData/>
     */
        </#if>
    public ${name}(<#rt>
        <#list referencedCompoundType.compoundParametersData.list as parameter>
            <#lt>${parameter.typeInfo.typeFullName} <@parameter_argument_name parameter/><#if parameter?has_next>,</#if><#rt>
        </#list>
            <#lt>)
    {
        super(<#rt>
        <#list referencedCompoundType.compoundParametersData.list as parameter>
            <#lt><@parameter_argument_name parameter/><#if parameter?has_next>,</#if><#rt>
        </#list>
        <#lt>);
    }

    </#if>
    <#if withCodeComments>
    /**
     * Read constructor.
     *
     * @param in Bit stream reader to use.
     <@compound_parameter_comments referencedCompoundType.compoundParametersData/>
     *
     * @throws IOException If the reading from bit stream failed.
     */
    </#if>
    public ${name}(zserio.runtime.io.BitStreamReader in<#if referencedCompoundType.compoundParametersData.list?has_content>,<#else>)</#if>
    <#list referencedCompoundType.compoundParametersData.list as parameter>
            ${parameter.typeInfo.typeFullName} <@parameter_argument_name parameter/><#if parameter?has_next>,<#else>)</#if>
    </#list>
            throws java.io.IOException
    {
        super(in<#rt>
    <#list referencedCompoundType.compoundParametersData.list as parameter>
            <#lt>, <@parameter_argument_name parameter/><#rt>
    </#list>
            <#lt>);
    }
    <#if referencedCompoundType.isPackable && referencedCompoundType.usedInPackedArray>

        <#if withCodeComments>
    /**
     * Read constructor.
     * <p>
     * Called only internally if packed arrays are used.
     *
     * @param context Context for packed arrays.
     * @param in Bit stream reader to use.
     <@compound_parameter_comments referencedCompoundType.compoundParametersData/>
     *
     * @throws IOException If the reading from bit stream failed.
     */
        </#if>
    public ${name}(zserio.runtime.array.PackingContext context, zserio.runtime.io.BitStreamReader in<#rt>
        <#lt><#if referencedCompoundType.compoundParametersData.list?has_content>,<#else>)</#if>
        <#list referencedCompoundType.compoundParametersData.list as parameter>
            ${parameter.typeInfo.typeFullName} <@parameter_argument_name parameter/><#if parameter?has_next>,<#else>)</#if>
        </#list>
            throws java.io.IOException
    {
        super(context, in<#rt>
        <#list referencedCompoundType.compoundParametersData.list as parameter>
            <#lt>, <@parameter_argument_name parameter/><#rt>
        </#list>
            <#lt>);
    }
    </#if>
    <#if isReferencedTypeStructure && withWriterCode && referencedCompoundType.fieldList?has_content>

        <#if withCodeComments>
    /**
     * Fields constructor.
     *
     <@compound_parameter_comments referencedCompoundType.compoundParametersData/>
           <#list referencedCompoundType.fieldList as field>
     * @param <@field_argument_name field/> Value of the field {@link #${field.getterName}() ${field.name}}.
            </#list>
     */
        </#if>
    public ${name}(
        <#list referencedCompoundType.compoundParametersData.list as parameter>
            ${parameter.typeInfo.typeFullName} <@parameter_argument_name parameter/>,
        </#list>
        <#list referencedCompoundType.fieldList as field>
            ${field.typeInfo.typeFullName} <@field_argument_name field/><#if field?has_next>,<#else>)</#if>
        </#list>
    {
        super(<#rt>
        <#list referencedCompoundType.compoundParametersData.list as parameter>
            <#lt><@parameter_argument_name parameter/>, <#rt>
        </#list>
        <#list referencedCompoundType.fieldList as field>
            <#lt><@field_argument_name field/><#if field?has_next>, </#if><#rt>
        </#list>
            <#lt>);
    }
    </#if>
</#if>
}
