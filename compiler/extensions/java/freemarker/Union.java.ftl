<#include "FileHeader.inc.ftl">
<#include "CompoundParameter.inc.ftl">
<#include "CompoundFunction.inc.ftl">
<#include "CompoundField.inc.ftl">
<#include "RangeCheck.inc.ftl">
<#include "TypeInfo.inc.ftl">
<#include "DocComment.inc.ftl">
<@standard_header generatorDescription, packageName/>
<#assign choiceTagArrayTraits="zserio.runtime.array.ArrayTraits.VarSizeArrayTraits">
<#assign choiceTagArrayElement="zserio.runtime.array.ArrayElement.IntArrayElement">

<#if withCodeComments && docComments??>
<@doc_comments docComments/>
</#if>
public class ${name} implements <#rt>
        <#if withWriterCode>zserio.runtime.io.<#if isPackable && usedInPackedArray>Packable</#if>Writer, <#t>
        <#lt></#if>zserio.runtime.<#if isPackable && usedInPackedArray>Packable</#if>SizeOf
{
<#if isPackable && usedInPackedArray>
    <@compound_declare_packing_context fieldList, true/>

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
    }

</#if>
<#if withCodeComments>
    /**
     * Read constructor.
     *
     * @param in Bit stream reader to use.
     <@compound_parameter_comments compoundParametersData/>
     *
     * @throws java.io.IOException If the reading from bit stream failed.
     */
</#if>
    public ${name}(zserio.runtime.io.BitStreamReader in<#if compoundParametersData.list?has_content>,<#else>)</#if>
<#list compoundParametersData.list as parameter>
            ${parameter.typeInfo.typeFullName} <@parameter_argument_name parameter/><#if parameter?has_next>,<#else>)</#if>
</#list>
            throws java.io.IOException
    {
        <@compound_set_parameters compoundParametersData/>
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
     * @throws java.io.IOException If the reading from bit stream failed.
     */
    </#if>
    public ${name}(zserio.runtime.array.PackingContext context, zserio.runtime.io.BitStreamReader in<#if compoundParametersData.list?has_content>,<#else>)</#if>
    <#list compoundParametersData.list as parameter>
            ${parameter.typeInfo.typeFullName} <@parameter_argument_name parameter/><#if parameter?has_next>,<#else>)</#if>
    </#list>
            throws java.io.IOException
    {
        <@compound_set_parameters compoundParametersData/>
    <#if compoundParametersData.list?has_content>

    </#if>
        read(context, in);
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

        return new zserio.runtime.typeinfo.TypeInfo.UnionTypeInfo(
                "${schemaTypeName}", ${name}.class, templateName, templateArguments,
                fieldList, parameterList, functionList
        );
    }
</#if>

<#if withCodeComments>
    /**
     * Gets the current choice tag.
     *
     * @return Choice tag which denotes chosen union field.
     */
</#if>
    public int choiceTag()
    {
        return choiceTag;
    }
<#if isPackable && usedInPackedArray>

    @Override
    public void initPackingContext(zserio.runtime.array.PackingContext context)
    {
        final ZserioPackingContext zserioContext = context.cast();
        zserioContext.choiceTag().init(
                new ${choiceTagArrayTraits}(),
                new ${choiceTagArrayElement}(choiceTag));

        switch (choiceTag)
        {
    <#list fieldList as field>
        case <@choice_tag_name field/>:
            <@compound_init_packing_context_field field, 3/>
            break;
    </#list>
        default:
            throw new zserio.runtime.ZserioError("No match in union ${name}!");
        }
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

        endBitPosition += zserio.runtime.BitSizeOfCalculator.getBitSizeOfVarSize(choiceTag);

        switch (choiceTag)
        {
    <#list fieldList as field>
        case <@choice_tag_name field/>:
            <@compound_bitsizeof_field field, 3/>
            break;
    </#list>
        default:
            throw new zserio.runtime.ZserioError("No match in union ${name}!");
        }

        return (int)(endBitPosition - bitPosition);
<#else>
        return 0;
</#if>
    }
<#if isPackable && usedInPackedArray>

    @Override
    public int bitSizeOf(zserio.runtime.array.PackingContext context, long bitPosition)
    {
        final ZserioPackingContext zserioContext = context.cast();
        long endBitPosition = bitPosition;

        endBitPosition += zserioContext.choiceTag().bitSizeOf(
                new ${choiceTagArrayTraits}(),
                new ${choiceTagArrayElement}(choiceTag));

        switch (choiceTag)
        {
        <#list fieldList as field>
        case <@choice_tag_name field/>:
            <@compound_bitsizeof_field field, 3, true/>
            break;
        </#list>
        default:
            throw new zserio.runtime.ZserioError("No match in union ${name}!");
        }

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
        return (objectChoice == null) ? null : ((${field.array.wrapperJavaTypeName})objectChoice).getRawArray();
    <#else>
        return (${field.nullableTypeInfo.typeFullName})objectChoice;
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
        choiceTag = <@choice_tag_name field/>;
        <#if field.array??>
        <#assign rawArray><@field_argument_name field/></#assign>
        objectChoice = <@array_wrapper_raw_constructor field, rawArray, 4/>;
        <#else>
        objectChoice = <@field_argument_name field/>;
        </#if>
    }

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
    <#if fieldList?has_content>
                    choiceTag == that.choiceTag &&
                    (
                        (objectChoice == null && that.objectChoice == null) ||
                        (objectChoice != null && objectChoice.equals(that.objectChoice))
                    );
    </#if>
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
        result = zserio.runtime.HashCodeUtil.calcHashCode(result, choiceTag);
<#if fieldList?has_content>
        if (objectChoice != null)
        {
            switch (choiceTag)
            {
    <#list fieldList as field>
            case <@choice_tag_name field/>:
                result = zserio.runtime.HashCodeUtil.calcHashCode(result,
        <#if field.array??>
                        (${field.array.wrapperJavaTypeName})objectChoice);
        <#else>
                        (${field.nullableTypeInfo.typeFullName})objectChoice);
        </#if>
                break;
    </#list>
            default:
                break; // UNDEFINED_CHOICE
            }
        }
</#if>

        return result;
    }

<#if withCodeComments>
    /**
     * Deserializes this Zserio object from the bit stream.
     *
     * @param in Bit stream reader to use.
     *
     * @throws java.io.IOException If the reading from the bit stream failed.
     */
</#if>
    public void read(zserio.runtime.io.BitStreamReader in) throws java.io.IOException
    {
<#if fieldList?has_content>
        choiceTag = in.readVarSize();

        switch (choiceTag)
        {
    <#list fieldList as field>
        case <@choice_tag_name field/>:
            <@compound_read_field field, name, 3/>
            <@compound_check_constraint_field field, name, 3/>
            break;
    </#list>
        default:
            throw new zserio.runtime.ZserioError("No match in union ${name}!");
        }
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
     * @throws java.io.IOException If the reading from the bit stream failed.
     */
    </#if>
    public void read(zserio.runtime.array.PackingContext context, zserio.runtime.io.BitStreamReader in)
            throws java.io.IOException
    {
        final ZserioPackingContext zserioContext = context.cast();
        choiceTag = ((${choiceTagArrayElement})
                zserioContext.choiceTag().read(
                        new ${choiceTagArrayTraits}(), in)).get();

        switch (choiceTag)
        {
    <#list fieldList as field>
        case <@choice_tag_name field/>:
            <@compound_read_field field, name, 3, true/>
            <@compound_check_constraint_field field, name, 3/>
            break;
    </#list>
        default:
            throw new zserio.runtime.ZserioError("No match in union ${name}!");
        }
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

        endBitPosition += zserio.runtime.BitSizeOfCalculator.getBitSizeOfVarSize(choiceTag);

        switch (choiceTag)
        {
        <#list fieldList as field>
        case <@choice_tag_name field/>:
            <@compound_initialize_offsets_field field, 3/>
            break;
        </#list>
        default:
            throw new zserio.runtime.ZserioError("No match in union ${name}!");
        }

        return endBitPosition;
    <#else>
        return bitPosition;
    </#if>
    }
    <#if isPackable && usedInPackedArray>

    @Override
    public long initializeOffsets(zserio.runtime.array.PackingContext context, long bitPosition)
    {
        final ZserioPackingContext zserioContext = context.cast();
        long endBitPosition = bitPosition;

        endBitPosition += zserioContext.choiceTag().bitSizeOf(
                new ${choiceTagArrayTraits}(),
                new ${choiceTagArrayElement}(choiceTag));

        switch (choiceTag)
        {
        <#list fieldList as field>
        case <@choice_tag_name field/>:
            <@compound_initialize_offsets_field field, 3, true/>
            break;
        </#list>
        default:
            throw new zserio.runtime.ZserioError("No match in union ${name}!");
        }

        return endBitPosition;
    }
    </#if>

    @Override
    public void write(zserio.runtime.io.BitStreamWriter out) throws java.io.IOException
    {
    <#if fieldList?has_content>
        out.writeVarSize(choiceTag);

        switch (choiceTag)
        {
        <#list fieldList as field>
        case <@choice_tag_name field/>:
            <@compound_check_constraint_field field, name, 3/>
            <@compound_write_field field, name, 3/>
            break;
        </#list>
        default:
            throw new zserio.runtime.ZserioError("No match in union ${name}!");
        };
    </#if>
    }
    <#if isPackable && usedInPackedArray>

    @Override
    public void write(zserio.runtime.array.PackingContext context, zserio.runtime.io.BitStreamWriter out)
            throws java.io.IOException
    {
        final ZserioPackingContext zserioContext = context.cast();
        zserioContext.choiceTag().write(
                new ${choiceTagArrayTraits}(), out,
                new ${choiceTagArrayElement}(choiceTag));

        switch (choiceTag)
        {
        <#list fieldList as field>
        case <@choice_tag_name field/>:
            <@compound_check_constraint_field field, name, 3/>
            <@compound_write_field field, name, 3, true/>
            break;
        </#list>
        default:
            throw new zserio.runtime.ZserioError("No match in union ${name}!");
        };
    }
    </#if>
</#if>

<#list fieldList as field>
    <#if withCodeComments>
    /** Choice tag which denotes chosen field ${field.name}. */
    </#if>
    public static final int <@choice_tag_name field/> = ${field?index};
</#list>
    <#if withCodeComments>
    /** Choice tag which is used if no field has been set yet. */
    </#if>
    public static final int UNDEFINED_CHOICE = -1;
<#list fieldList as field>
    <@define_field_helper_classes name, field/>
</#list>

    <@compound_parameter_members compoundParametersData/>
    private int choiceTag = UNDEFINED_CHOICE;
<#if fieldList?has_content>
    private java.lang.Object objectChoice;
</#if>
}
