<#include "FileHeader.inc.ftl">
<#include "CompoundParameter.inc.ftl">
<#include "CompoundFunction.inc.ftl">
<#include "CompoundField.inc.ftl">
<#include "RangeCheck.inc.ftl">
<#include "TypeInfo.inc.ftl">
<#include "DocComment.inc.ftl">
<@standard_header generatorDescription, packageName/>
<#assign hasFieldWithConstraint=false/>
<#list fieldList as field>
    <#if field.constraint??>
        <#assign hasFieldWithConstraint=true/>
        <#break>
    </#if>
</#list>

<#if withCodeComments && docComments??>
<@doc_comments docComments/>
</#if>
<#assign numExtendedFields=num_extended_fields(fieldList)>
<#function extended_field_index numFields numExtendedFields fieldIndex>
    <#return fieldIndex - (numFields - numExtendedFields)>
</#function>
public class ${name} implements <#rt>
        <#if withWriterCode>zserio.runtime.io.<#if isPackable && usedInPackedArray>Packable</#if>Writer, <#t>
        <#lt></#if>zserio.runtime.<#if isPackable && usedInPackedArray>Packable</#if>SizeOf
{
<#if isPackable && usedInPackedArray>
    <@compound_declare_packing_context fieldList/>

</#if>
<#if withWriterCode>
    <#if withCodeComments>
    /**
     * Default constructor.
     *
     <@compound_parameter_comments compoundParametersData/>
     */
    </#if>
    public ${name}(<#if !compoundParametersData.list?has_content>)</#if>
    <#list compoundParametersData.list as parameter>
            ${parameter.typeInfo.typeFullName} <@parameter_argument_name parameter/><#if parameter?has_next>,<#else>)</#if>
    </#list>
    {
        <@compound_set_parameters compoundParametersData/>
    <#if (numExtendedFields > 0)>
        this.numExtendedFields = ${numExtendedFields};
    </#if>
    }

</#if>
<#if withCodeComments>
    /**
     * Read constructor.
     *
     * @param in Bit stream reader to use.
     <@compound_parameter_comments compoundParametersData/>
     *
     * @throws IOException If the reading from bit stream failed.
     */
</#if>
    public ${name}(zserio.runtime.io.BitStreamReader in<#if compoundParametersData.list?has_content>,<#else>)</#if>
<#list compoundParametersData.list as parameter>
            ${parameter.typeInfo.typeFullName} <@parameter_argument_name parameter/><#if parameter?has_next>,<#else>)</#if>
</#list>
            throws java.io.IOException
    {
        <@compound_set_parameters compoundParametersData/>
<#if (numExtendedFields > 0)>
        this.numExtendedFields = 0;
</#if>
<#if compoundParametersData.list?has_content>

</#if>
        read(in);
    }
<#if isPackable && usedInPackedArray>

    <#if withCodeComments>
    /**
     * Read constructor.
     * <p>
     * Called only internally if packed arrays are used.
     *
     * @param context Context for packed arrays.
     * @param in Bit stream reader to use.
     <@compound_parameter_comments compoundParametersData/>
     *
     * @throws IOException If the reading from bit stream failed.
     */
    </#if>
    public ${name}(zserio.runtime.array.PackingContext context, zserio.runtime.io.BitStreamReader in<#if compoundParametersData.list?has_content>,<#else>)</#if>
    <#list compoundParametersData.list as parameter>
            ${parameter.typeInfo.typeFullName} <@parameter_argument_name parameter/><#if parameter?has_next>,<#else>)</#if>
    </#list>
            throws java.io.IOException
    {
        <@compound_set_parameters compoundParametersData/>
    <#if (numExtendedFields > 0)>
        this.numExtendedFields = 0;
    </#if>
    <#if compoundParametersData.list?has_content>

    </#if>
        read(context, in);
    }
</#if>
<#if withWriterCode && fieldList?has_content>

    <#if withCodeComments>
    /**
     * Fields constructor.
     *
     <@compound_parameter_comments compoundParametersData/>
        <#list fieldList as field>
     * @param <@field_argument_name field/> Value of the field {@link #${field.getterName}() ${field.name}}.
        </#list>
     */
    </#if>
    public ${name}(
    <#list compoundParametersData.list as parameter>
            ${parameter.typeInfo.typeFullName} <@parameter_argument_name parameter/>,
    </#list>
    <#list fieldList as field>
            ${field.typeInfo.typeFullName} <@field_argument_name field/><#if field?has_next>,<#else>)</#if>
    </#list>
    {
    <#if compoundParametersData.list?has_content>
        this(<#rt>
        <#list compoundParametersData.list as parameter>
                <#lt><@parameter_argument_name parameter/><#if parameter?has_next>, </#if><#rt>
        </#list>
                <#lt>);

    </#if>
    <#list fieldList as field>
        ${field.setterName}(<@field_argument_name field/>);
    </#list>
    }
</#if>
<#if withTypeInfoCode>

    <#if withCodeComments>
    /**
     * Gets static information about this Zserio type useful for generic introspection.
     *
     * @return Zserio type information.
     */
    </#if>
    public static zserio.runtime.typeinfo.TypeInfo typeInfo()
    {
    <#list fieldList as field>
        <@field_info_recursive_type_info_getter field/>
    </#list>
        final java.lang.String templateName = <@template_info_template_name templateInstantiation!/>;
        final java.util.List<zserio.runtime.typeinfo.TypeInfo> templateArguments =
                <@template_info_template_arguments templateInstantiation!/>
        final java.util.List<zserio.runtime.typeinfo.FieldInfo> fieldList =
                <@fields_info fieldList/>
        final java.util.List<zserio.runtime.typeinfo.ParameterInfo> parameterList =
                <@parameters_info compoundParametersData.list/>
        final java.util.List<zserio.runtime.typeinfo.FunctionInfo> functionList =
                <@functions_info compoundFunctionsData.list/>

        return new zserio.runtime.typeinfo.TypeInfo.StructTypeInfo(
                "${schemaTypeName}", ${name}.class, templateName, templateArguments,
                fieldList, parameterList, functionList
        );
    }
</#if>
<#if isPackable && usedInPackedArray>

    @Override
    public void initPackingContext(zserio.runtime.array.PackingContext context)
    {
    <#if uses_packing_context(fieldList)>
        final ZserioPackingContext zserioContext = context.cast();
        <#list fieldList as field>
        <@compound_init_packing_context_field field, 2/>
        </#list>
    </#if>
    }
</#if>

    @Override
    public int bitSizeOf()
    {
        return bitSizeOf(0);
    }

    @Override
    public int bitSizeOf(long bitPosition)
    {
<#if fieldList?has_content>
        long endBitPosition = bitPosition;

    <#list fieldList as field>
        <@compound_bitsizeof_field field, 2/>
    </#list>

        return (int)(endBitPosition - bitPosition);
<#else>
        return 0;
</#if>
    }
<#if isPackable && usedInPackedArray>

    @Override
    public int bitSizeOf(zserio.runtime.array.PackingContext context, long bitPosition)
    {
    <#if uses_packing_context(fieldList)>
        final ZserioPackingContext zserioContext = context.cast();
    </#if>
        long endBitPosition = bitPosition;

    <#list fieldList as field>
        <@compound_bitsizeof_field field, 2, true/>
    </#list>

        return (int)(endBitPosition - bitPosition);
    }
</#if>

    <@compound_parameter_accessors compoundParametersData/>
<#list fieldList as field>
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
    public ${field.typeInfo.typeFullName} ${field.getterName}()
    {
    <#if field.array??>
        return (<@field_member_name field/> == null) ? null : <@field_member_name field/>.getRawArray();
    <#else>
        return <@field_member_name field/>;
    </#if>
    }

    <#if withWriterCode>
        <#if withCodeComments>
    /**
     * Sets the field ${field.name}.
            <#if field.docComments??>
     * <p>
     * <b>Description:</b>
     * <br>
     <@doc_comments_inner field.docComments, 1/>
     *
            <#else>
     *
            </#if>
     * @param <@field_argument_name field/> Value of the field ${field.name} to set.
     */
        </#if>
    public void ${field.setterName}(${field.typeInfo.typeFullName} <@field_argument_name field/>)
    {
        <@range_check field.rangeCheckData, name/>
        <#if field.isExtended>
        if (!${field.isPresentIndicatorName}())
            this.numExtendedFields = ${numExtendedFields};
        </#if>
        <#if field.array??>
            <#assign rawArray><@field_argument_name field/></#assign>
            <#if field.optional??>
        if (<@field_argument_name field/> == null)
        {
            this.<@field_member_name field/> = null;
        }
        else
        {
            this.<@field_member_name field/> = <@array_wrapper_raw_constructor field, rawArray, 5/>;
        }
            <#else>
        this.<@field_member_name field/> = <@array_wrapper_raw_constructor field, rawArray, 4/>;
            </#if>
        <#else>
        this.<@field_member_name field/> = <@field_argument_name field/>;
        </#if>
    }

    </#if>
    <#if field.isExtended>
        <#if withCodeComments>
    /**
     * Checks if the extended field ${field.name} is present.
     *
     * @return True if the extended field ${field.name} is present, otherwise false.
     */
        </#if>
    public boolean ${field.isPresentIndicatorName}()
    {
        return numExtendedFields > ${extended_field_index(fieldList?size, numExtendedFields, field?index)};
    }

    </#if>
    <#if field.optional??>
        <#if withCodeComments>
    /**
     * Checks if the optional field ${field.name} is used during serialization and deserialization.
     *
     * @return True if the optional field ${field.name} is used, otherwise false.
     */
        </#if>
    public boolean ${field.optional.isUsedIndicatorName}()
    {
        return <#if field.optional.clause??>(${field.optional.clause});<#else>${field.optional.isSetIndicatorName}();</#if>
    }

        <#if withWriterCode>
            <#if withCodeComments>
    /**
     * Checks if the optional field ${field.name} is set.
     *
     * @return True if the optional field ${field.name} is set, otherwise false.
     */
            </#if>
    public boolean ${field.optional.isSetIndicatorName}()
    {
        return (<@field_member_name field/> != null);
    }

            <#if withCodeComments>
    /**
     * Resets the optional field ${field.name}.
     */
            </#if>
    public void ${field.optional.resetterName}()
    {
            <#if field.isExtended>
        if (!${field.isPresentIndicatorName}())
            this.numExtendedFields = ${numExtendedFields};
            </#if>
        this.<@field_member_name field/> = null;
    }

        </#if>
    </#if>
</#list>
<@compound_functions compoundFunctionsData/>
    @Override
    public boolean equals(java.lang.Object obj)
    {
<#if compoundParametersData.list?has_content || fieldList?has_content>
        if (obj instanceof ${name})
        {
            final ${name} that = (${name})obj;

            return
    <#list compoundParametersData.list as parameter>
                    <@compound_compare_parameter parameter/><#if parameter?has_next || fieldList?has_content> &&<#else>;</#if>
    </#list>
    <#list fieldList as field>
        <#if field.optional??>
            <#-- if optional is not auto and is used the that should be is used as well because all previous paramaters and fields were the same. -->
                    ((!${field.optional.isUsedIndicatorName}()) ? !that.${field.optional.isUsedIndicatorName}() :
                        <@compound_compare_field field/>)<#if field?has_next> &&<#else>;</#if>
        <#else>
                    <@compound_compare_field field/><#if field?has_next> &&<#else>;</#if>
        </#if>
    </#list>
        }

        return false;
<#else>
        return obj instanceof ${name};
</#if>
    }

    @Override
    public int hashCode()
    {
        int result = zserio.runtime.HashCodeUtil.HASH_SEED;

        <@compound_parameter_hash_code compoundParametersData/>
<#list fieldList as field>
    <#if field.optional??>
        if (${field.optional.isUsedIndicatorName}())
            result = zserio.runtime.HashCodeUtil.calcHashCode(result, <@field_member_name field/>);
    <#else>
        result = zserio.runtime.HashCodeUtil.calcHashCode(result, <@field_member_name field/>);
    </#if>
    <#if !field?has_next>

    </#if>
</#list>
        return result;
    }

<#if withCodeComments>
    /**
     * Deserializes this Zserio object from the bit stream.
     *
     * @param in Bit stream reader to use.
     *
     * @throws IOException If the reading from the bit stream failed.
     */
</#if>
    public void read(zserio.runtime.io.BitStreamReader in)
            throws java.io.IOException
    {
<#if fieldList?has_content>
    <#list fieldList as field>
        <#if field.isExtended>
    if (zserio.runtime.BitPositionUtil.alignTo(java.lang.Byte.SIZE, in.getBitPosition()) < in.getBufferBitSize())
    {
        ++this.numExtendedFields;
        in.alignTo(java.lang.Byte.SIZE);

        <@compound_read_field field, name, 3/>
    }
        <#else>
    <@compound_read_field field, name, 2/>
        </#if>
        <#if field?has_next>

        </#if>
    </#list>
    <#if hasFieldWithConstraint>

        checkConstraints();
    </#if>
</#if>
    }
<#if isPackable && usedInPackedArray>

    <#if withCodeComments>
    /**
     * Deserializes this Zserio object from the bit stream.
     * <p>
     * Called only internally if packed arrays are used.
     *
     * @param context Context for packed arrays.
     * @param in Bit stream reader to use.
     *
     * @throws IOException If the reading from the bit stream failed.
     */
    </#if>
    public void read(zserio.runtime.array.PackingContext context, zserio.runtime.io.BitStreamReader in)
            throws java.io.IOException
    {
    <#if uses_packing_context(fieldList)>
        final ZserioPackingContext zserioContext = context.cast();
    </#if>
    <#list fieldList as field>
        <#if field.isExtended>
    if (zserio.runtime.BitPositionUtil.alignTo(java.lang.Byte.SIZE, in.getBitPosition()) < in.getBufferBitSize())
    {
        ++this.numExtendedFields;
        in.alignTo(java.lang.Byte.SIZE);

        <@compound_read_field field, name, 3, true/>
    }
        <#else>
    <@compound_read_field field, name, 2, true/>
        </#if>
        <#if field?has_next>

        </#if>
    </#list>
    <#if hasFieldWithConstraint>

        checkConstraints();
    </#if>
    }
</#if>
<#if withWriterCode>

    @Override
    public long initializeOffsets()
    {
        return initializeOffsets(0);
    }

    @Override
    public long initializeOffsets(long bitPosition)
    {
    <#if fieldList?has_content>
        long endBitPosition = bitPosition;

        <#list fieldList as field>
        <@compound_initialize_offsets_field field, 2/>
        </#list>

        return endBitPosition;
    <#else>
        return bitPosition;
    </#if>
    }
    <#if isPackable && usedInPackedArray>

    @Override
    public long initializeOffsets(zserio.runtime.array.PackingContext context, long bitPosition)
    {
        <#if uses_packing_context(fieldList)>
        final ZserioPackingContext zserioContext = context.cast();
        </#if>
        long endBitPosition = bitPosition;

        <#list fieldList as field>
        <@compound_initialize_offsets_field field, 2, true/>
        </#list>

        return endBitPosition;
    }
    </#if>

    @Override
    public void write(zserio.runtime.io.BitStreamWriter out) throws java.io.IOException
    {
    <#if fieldList?has_content>
        <#if hasFieldWithConstraint>
        checkConstraints();

        </#if>
        <#list fieldList as field>
        <@compound_write_field field, name, 2/>
            <#if field?has_next>

            </#if>
        </#list>
    </#if>
    }
    <#if isPackable && usedInPackedArray>

    @Override
    public void write(zserio.runtime.array.PackingContext context, zserio.runtime.io.BitStreamWriter out)
            throws java.io.IOException
    {
        <#if uses_packing_context(fieldList)>
        final ZserioPackingContext zserioContext = context.cast();
        </#if>
        <#if hasFieldWithConstraint>
        checkConstraints();

        </#if>
        <#list fieldList as field>
        <@compound_write_field field, name, 2, true/>
            <#if field?has_next>

            </#if>
        </#list>
    }
    </#if>
</#if>
<#if hasFieldWithConstraint>

    private void checkConstraints()
    {
    <#list fieldList as field>
        <@compound_check_constraint_field field, name, 2/>
    </#list>
    }
</#if>
<#list fieldList as field>
    <@define_field_helper_classes name, field/>
</#list>
<#if compoundParametersData.list?has_content || fieldList?has_content>

</#if>
<@compound_parameter_members compoundParametersData/>
<#if (numExtendedFields > 0)>
    private int numExtendedFields;
</#if>
<#macro field_java_type_member_name field>
    <#if field.array??>
        ${field.array.wrapperJavaTypeName}<#t>
    <#else>
        ${field.typeInfo.typeFullName}<#t>
    </#if>
</#macro>
<#list fieldList as field>
    private <@field_java_type_member_name field/> <@field_member_name field/><#rt>
    <#if field.initializer??>
        <#lt> = ${field.initializer}<#rt>
    </#if>
    <#lt>;
</#list>
}
