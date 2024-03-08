<#include "FileHeader.inc.ftl">
<#include "CompoundParameter.inc.ftl">
<#include "CompoundFunction.inc.ftl">
<#include "CompoundField.inc.ftl">
<#include "RangeCheck.inc.ftl">
<#include "TypeInfo.inc.ftl">
<#include "DocComment.inc.ftl">
<@standard_header generatorDescription, packageName/>
<#macro choice_selector_condition caseList>
    <#if isSelectorExpressionBigInteger>
        <#list caseList as case>
            <#if case_index != 0>
 ||
                 </#if>selector.compareTo(new java.math.BigInteger("${case.expressionForIf}")) == 0<#rt>
        </#list>
    <#elseif selectorExpressionBitmaskTypeName??>
        <#list caseList as case>
            <#if case_index != 0>
 ||
                 </#if>selector.equals(${case.expressionForIf})<#rt>
        </#list>
    <#else>
        <#list caseList as case>
selector == (${case.expressionForIf})<#if case?has_next> || </#if><#rt>
        </#list>
    </#if>
</#macro>
<#macro choice_no_match name indent>
    <#local I>${""?left_pad(indent * 4)}</#local>
${I}throw new zserio.runtime.ZserioError("No match in choice ${name}: " + ${selectorExpression} + "!");
</#macro>
<#assign isSwitchAllowed = !isSelectorExpressionBoolean && !isSelectorExpressionBigInteger &&
        !isSelectorExpressionLong && !selectorExpressionBitmaskTypeName??>
<#macro choice_switch memberActionMacroName noMatchMacroName indent packed=false>
    <#local I>${""?left_pad(indent * 4)}</#local>
    <#if isSwitchAllowed>
${I}switch (${selectorExpression})
${I}{
        <#list caseMemberList as caseMember>
            <#list caseMember.caseList as case>
${I}case ${case.expressionForCase}:
            </#list>
        <@.vars[memberActionMacroName] caseMember, indent + 1, packed/>
        </#list>
        <#if !isDefaultUnreachable>
${I}default:
            <#if defaultMember??>
        <@.vars[memberActionMacroName] defaultMember, indent + 1, packed/>
            <#else>
        <@.vars[noMatchMacroName] name, indent+1/>
            </#if>
        </#if>
${I}}
    <#else>
        <#if isSelectorExpressionBoolean>
${I}final boolean selector = ${selectorExpression};
        <#elseif isSelectorExpressionLong>
${I}final long selector = ${selectorExpression};
        <#elseif selectorExpressionBitmaskTypeName??>
${I}final ${selectorExpressionBitmaskTypeName} selector = ${selectorExpression};
        <#else>
${I}final java.math.BigInteger selector = ${selectorExpression};
        </#if>

        <#list caseMemberList as caseMember>
            <#if caseMember?has_next || !isDefaultUnreachable>
${I}<#if caseMember_index != 0>else </#if>if (<@choice_selector_condition caseMember.caseList/>)
            <#else>
${I}else
            </#if>
${I}{
        <@.vars[memberActionMacroName] caseMember, indent + 1, packed/>
${I}}
        </#list>
        <#if !isDefaultUnreachable>
${I}else
${I}{
            <#if defaultMember??>
        <@.vars[memberActionMacroName] defaultMember, indent + 1, packed/>
            <#else>
        <@.vars[noMatchMacroName] name, indent+1/>
            </#if>
${I}}
        </#if>
    </#if>
</#macro>

<#if withCodeComments && docComments??>
<@doc_comments docComments/>
</#if>
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
        final java.util.List<zserio.runtime.typeinfo.CaseInfo> caseList =
                <@cases_info caseMemberList, defaultMember!/>

        return new zserio.runtime.typeinfo.TypeInfo.ChoiceTypeInfo(
                "${schemaTypeName}", ${name}.class, templateName, templateArguments,
                fieldList, parameterList, functionList, "${selectorExpression?j_string}", caseList
        );
    }
</#if>

<#macro choice_tag_no_match name indent>
    <#local I>${""?left_pad(indent * 4)}</#local>
${I}return UNDEFINED_CHOICE;
</#macro>
<#macro choice_tag_member member indent packed>
    <#local I>${""?left_pad(indent * 4)}</#local>
    <#if member.compoundField??>
${I}return <@choice_tag_name member.compoundField/>;
    <#else>
${I}return UNDEFINED_CHOICE;
    </#if>
</#macro>
    <#if withCodeComments>
    /**
     * Gets the current choice tag.
     *
     * @return Choice tag which denotes chosen field.
     */
    </#if>
    public int choiceTag()
    {
<#if fieldList?has_content>
        <@choice_switch "choice_tag_member", "choice_tag_no_match", 2/>
<#else>
        return UNDEFINED_CHOICE;
</#if>
    }
<#if isPackable && usedInPackedArray>

<#macro choice_init_packing_context_member member indent packed>
    <#local I>${""?left_pad(indent * 4)}</#local>
    <#if member.compoundField??>
        <@compound_init_packing_context_field member.compoundField, indent/>
    <#else>
${I}// empty
    </#if>
    <#if isSwitchAllowed>
${I}break;
    </#if>
</#macro>
    @Override
    public void initPackingContext(zserio.runtime.array.PackingContext context)
    {
    <#if uses_packing_context(fieldList)>
        final ZserioPackingContext zserioContext = context.cast();
        <@choice_switch "choice_init_packing_context_member", "choice_no_match", 2, true/>
    </#if>
    }
</#if>

    @Override
    public int bitSizeOf()
    {
        return bitSizeOf(0);
    }

<#macro choice_bitsizeof_member member indent packed>
    <#local I>${""?left_pad(indent * 4)}</#local>
    <#if member.compoundField??>
        <@compound_bitsizeof_field member.compoundField, indent, packed/>
    <#else>
${I}// empty
    </#if>
    <#if isSwitchAllowed>
${I}break;
    </#if>
</#macro>
    @Override
    public int bitSizeOf(long bitPosition)
    {
<#if fieldList?has_content>
        long endBitPosition = bitPosition;

        <@choice_switch "choice_bitsizeof_member", "choice_no_match", 2/>

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

        <@choice_switch "choice_bitsizeof_member", "choice_no_match", 2, true/>

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

<#macro choice_hash_code_no_match name indent>
    <#local I>${""?left_pad(indent * 4)}</#local>
    <#if isSwitchAllowed>
${I}break;
    </#if>
</#macro>
<#macro choice_hash_code_member member indent packed>
    <#local I>${""?left_pad(indent * 4)}</#local>
    <#if member.compoundField??>
${I}result = zserio.runtime.HashCodeUtil.calcHashCode(result, <#rt>
        <#if member.compoundField.array??>
        <#lt>(${member.compoundField.array.wrapperJavaTypeName})objectChoice);
        <#else>
        <#lt>(${member.compoundField.nullableTypeInfo.typeFullName})objectChoice);
        </#if>
    <#else>
${I}// empty
    </#if>
    <#if isSwitchAllowed>
${I}break;
    </#if>
</#macro>
    @Override
    public int hashCode()
    {
        int result = zserio.runtime.HashCodeUtil.HASH_SEED;

        <@compound_parameter_hash_code compoundParametersData/>
<#if fieldList?has_content>
        if (objectChoice != null)
        {
            <@choice_switch "choice_hash_code_member", "choice_hash_code_no_match", 3/>
        }
</#if>

        return result;
    }

<#macro choice_read_member member indent packed>
    <#local I>${""?left_pad(indent * 4)}</#local>
    <#if member.compoundField??>
        <@compound_read_field member.compoundField, name, indent, packed/>
        <@compound_check_constraint_field member.compoundField, name, indent/>
    <#else>
${I}// empty
    </#if>
    <#if isSwitchAllowed>
${I}break;
    </#if>
</#macro>
<#if withCodeComments>
    /**
     * Deserializes this Zserio object from the bit stream.
     *
     * @param in Bit stream reader to use.
     *
     * @throws IOException If the reading from the bit stream failed.
     */
</#if>
    public void read(zserio.runtime.io.BitStreamReader in) throws java.io.IOException
    {
<#if fieldList?has_content>
        <@choice_switch "choice_read_member", "choice_no_match", 2/>
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
        <@choice_switch "choice_read_member", "choice_no_match", 2, true/>
    }
</#if>
<#if withWriterCode>

    @Override
    public long initializeOffsets()
    {
        return initializeOffsets(0);
    }

<#macro choice_initialize_offsets_member member indent packed>
    <#local I>${""?left_pad(indent * 4)}</#local>
    <#if member.compoundField??>
        <#if isSwitchAllowed>
${I}{
        <@compound_initialize_offsets_field member.compoundField, indent + 1, packed/>
${I}}
        <#else>
    <@compound_initialize_offsets_field member.compoundField, indent, packed/>
        </#if>
    <#else>
${I}// empty
    </#if>
    <#if isSwitchAllowed>
${I}break;
    </#if>
</#macro>
    @Override
    public long initializeOffsets(long bitPosition)
    {
    <#if fieldList?has_content>
        long endBitPosition = bitPosition;

        <@choice_switch "choice_initialize_offsets_member", "choice_no_match", 2/>

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

        <@choice_switch "choice_initialize_offsets_member", "choice_no_match", 2, true/>

        return endBitPosition;
    }
</#if>

<#macro choice_write_member member indent packed>
    <#local I>${""?left_pad(indent * 4)}</#local>
    <#if member.compoundField??>
        <@compound_check_constraint_field member.compoundField, name, indent/>
        <@compound_write_field member.compoundField, name, indent, packed/>
    <#else>
${I}// empty
    </#if>
    <#if isSwitchAllowed>
${I}break;
    </#if>
</#macro>
    @Override
    public void write(zserio.runtime.io.BitStreamWriter out) throws java.io.IOException
    {
    <#if fieldList?has_content>
        <@choice_switch "choice_write_member", "choice_no_match", 2/>
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
        <@choice_switch "choice_write_member", "choice_no_match", 2, true/>
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
<#if fieldList?has_content>
    private java.lang.Object objectChoice;
</#if>
}
