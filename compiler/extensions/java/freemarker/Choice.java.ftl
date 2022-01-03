<#include "FileHeader.inc.ftl">
<#include "CompoundConstructor.inc.ftl">
<#include "CompoundParameter.inc.ftl">
<#include "CompoundFunction.inc.ftl">
<#include "CompoundField.inc.ftl">
<#include "RangeCheck.inc.ftl">
<#include "TypeInfo.inc.ftl">
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
selector == (${case.expressionForIf})<#if case_has_next> || </#if><#rt>
        </#list>
    </#if>
</#macro>
<#assign isSwitchAllowed = !isSelectorExpressionBoolean && !isSelectorExpressionBigInteger &&
        !isSelectorExpressionLong && !selectorExpressionBitmaskTypeName??>
<#macro choice_switch memberActionMacroName indent packed=false>
    <#local I>${""?left_pad(indent * 4)}</#local>
    <#local fieldIndex=0>
    <#if isSwitchAllowed>
${I}switch (${selectorExpression})
${I}{
        <#list caseMemberList as caseMember>
            <#list caseMember.caseList as case>
${I}case ${case.expressionForCase}:
            </#list>
        <@.vars[memberActionMacroName] caseMember, indent + 1, packed, fieldIndex/>
            <#if caseMember.compoundField??><#local fieldIndex+=1></#if>
${I}    break;
        </#list>
        <#if !isDefaultUnreachable>
${I}default:
            <#if defaultMember??>
        <@.vars[memberActionMacroName] defaultMember, indent + 1, packed, fieldIndex/>
                <#if defaultMember.compoundField??><#local fieldIndex+=1></#if>
${I}    break;
            <#else>
${I}    throw new zserio.runtime.ZserioError("No match in choice ${name}: " + ${selectorExpression} + "!");
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
            <#if caseMember_has_next || !isDefaultUnreachable>
${I}<#if caseMember_index != 0>else </#if>if (<@choice_selector_condition caseMember.caseList/>)
            <#else>
${I}else
            </#if>
${I}{
        <@.vars[memberActionMacroName] caseMember, indent + 1, packed, fieldIndex/>
            <#if caseMember.compoundField??><#local fieldIndex+=1></#if>
${I}}
        </#list>
        <#if !isDefaultUnreachable>
${I}else
${I}{
            <#if defaultMember??>
        <@.vars[memberActionMacroName] defaultMember, indent + 1, packed, fieldIndex/>
                <#if defaultMember.compoundField??><#local fieldIndex+=1></#if>
            <#else>
${I}    throw new zserio.runtime.ZserioError("No match in choice ${name}: " + ${selectorExpression} + "!");
            </#if>
${I}}
        </#if>
    </#if>
</#macro>

public class ${name} implements <#if withWriterCode>zserio.runtime.io.InitializeOffsetsWriter, </#if>zserio.runtime.SizeOf
{
    <@compound_constructors compoundConstructorsData/>
<#if withTypeInfoCode>
    public static zserio.runtime.typeinfo.TypeInfo typeInfo()
    {
    <#list fieldList as field>
        <@field_info_recursive_type_info_getter field/>
    </#list>
        final java.lang.String templateName = <@template_info_template_name templateInstantiation!/>;
        final java.util.List<zserio.runtime.typeinfo.TypeInfo> templateArguments =
                <@template_info_template_arguments templateInstantiation!/>
        final java.util.List<zserio.runtime.typeinfo.FieldInfo> fields =
                <@fields_info fieldList/>
        final java.util.List<zserio.runtime.typeinfo.ParameterInfo> parameters =
                <@parameters_info compoundParametersData.list/>
        final java.util.List<zserio.runtime.typeinfo.FunctionInfo> functions =
                <@functions_info compoundFunctionsData.list/>
        final java.util.List<zserio.runtime.typeinfo.CaseInfo> cases =
                <@cases_info caseMemberList, defaultMember!, isSwitchAllowed/>

        return new zserio.runtime.typeinfo.TypeInfo.ChoiceTypeInfo(
                "${schemaTypeName}", templateName, templateArguments,
                fields, parameters, functions, "${selectorExpression?j_string}", cases
        );
    }

</#if>
    public static void createPackingContext(zserio.runtime.array.PackingContextNode contextNode)
    {
<#list fieldList as field>
    <@compound_create_packing_context_field field/>
</#list>
    }

<#function choice_needs_init_packing_context fieldList>
    <#list fieldList as field>
        <#if field.isPackable && !field.array??>
            <#return true>
        </#if>
    </#list>
    <#return false>
</#function>
<#macro choice_init_packing_context_member member indent packed index>
    <#if member.compoundField??>
        <@compound_init_packing_context_field member.compoundField, index, indent/>
    <#else>
        <#local I>${""?left_pad(indent * 4)}</#local>
        <#lt>${I}// empty
    </#if>
</#macro>
    @Override
    public void initPackingContext(zserio.runtime.array.PackingContextNode contextNode)
    {
<#if choice_needs_init_packing_context(fieldList)>
        <@choice_switch "choice_init_packing_context_member", 2, true/>
</#if>
    }

    @Override
    public int bitSizeOf()
    {
        return bitSizeOf(0);
    }

<#macro choice_bitsizeof_member member indent packed index>
    <#if member.compoundField??>
        <@compound_bitsizeof_field member.compoundField, indent, packed, index/>
    <#else>
        <#local I>${""?left_pad(indent * 4)}</#local>
        <#lt>${I}// empty
    </#if>
</#macro>
    @Override
    public int bitSizeOf(long bitPosition)
    {
<#if fieldList?has_content>
        long endBitPosition = bitPosition;

        <@choice_switch "choice_bitsizeof_member", 2/>

        return (int)(endBitPosition - bitPosition);
<#else>
        return 0;
</#if>
    }

    @Override
    public int bitSizeOf(zserio.runtime.array.PackingContextNode contextNode, long bitPosition)
    {
<#if fieldList?has_content>
        long endBitPosition = bitPosition;

        <@choice_switch "choice_bitsizeof_member", 2, true/>

        return (int)(endBitPosition - bitPosition);
<#else>
        return 0;
</#if>
    }

<@compound_parameter_accessors compoundParametersData/>
<#list fieldList as field>
    public ${field.javaTypeName} ${field.getterName}()
    {
    <#if field.array??>
        return ((${field.array.wrapperJavaTypeName})objectChoice).getRawArray();
    <#else>
        return (${field.javaNullableTypeName})objectChoice;
    </#if>
    }

    <#if withWriterCode>
    public void ${field.setterName}(${field.javaTypeName} <@field_argument_name field/>)
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
        if (obj instanceof ${name})
        {
            final ${name} that = (${name})obj;

            return
<#list compoundParametersData.list as parameter>
                    <@compound_compare_parameter parameter/> &&
</#list>
                    (
                        (objectChoice == null && that.objectChoice == null) ||
                        (objectChoice != null && objectChoice.equals(that.objectChoice))
                    );
        }

        return false;
    }

    @Override
    public int hashCode()
    {
        int result = zserio.runtime.Util.HASH_SEED;

<#list compoundParametersData.list as parameter>
        <@compound_hashcode_parameter parameter/>
</#list>
        result = zserio.runtime.Util.HASH_PRIME_NUMBER * result +
                ((objectChoice == null) ? 0 : objectChoice.hashCode());

        return result;
    }

<#macro choice_read_member member indent packed index>
    <#if member.compoundField??>
        <@compound_read_field member.compoundField, name, indent, packed, index/>
        <@compound_check_constraint_field member.compoundField, name, indent/>
    <#else>
        <#local I>${""?left_pad(indent * 4)}</#local>
        <#lt>${I}// empty
    </#if>
</#macro>
    public void read(zserio.runtime.io.BitStreamReader in) throws java.io.IOException
    {
<#if fieldList?has_content>
        <@choice_switch "choice_read_member", 2/>
</#if>
    }

    public void read(zserio.runtime.array.PackingContextNode contextNode, zserio.runtime.io.BitStreamReader in)
            throws java.io.IOException
    {
<#if fieldList?has_content>
        <@choice_switch "choice_read_member", 2, true/>
</#if>
    }
<#if withWriterCode>

<#macro choice_initialize_offsets_member member indent packed index>
    <#local I>${""?left_pad(indent * 4)}</#local>
    <#if member.compoundField??>
        <#if isSwitchAllowed>
${I}{
        <@compound_initialize_offsets_field member.compoundField, indent + 1, packed, index/>
${I}}
        <#else>
    <@compound_initialize_offsets_field member.compoundField, indent, packed, index/>
        </#if>
    <#else>
${I}// empty
    </#if>
</#macro>
    public long initializeOffsets(long bitPosition)
    {
    <#if fieldList?has_content>
        long endBitPosition = bitPosition;

        <@choice_switch "choice_initialize_offsets_member", 2/>

        return endBitPosition;
    <#else>
        return bitPosition;
    </#if>
    }

    public long initializeOffsets(zserio.runtime.array.PackingContextNode contextNode, long bitPosition)
    {
    <#if fieldList?has_content>
        long endBitPosition = bitPosition;

        <@choice_switch "choice_initialize_offsets_member", 2, true/>

        return endBitPosition;
    <#else>
        return bitPosition;
    </#if>
    }

    public void write(java.io.File file) throws java.io.IOException
    {
        zserio.runtime.io.FileBitStreamWriter out = new zserio.runtime.io.FileBitStreamWriter(file);
        write(out);
        out.close();
    }

    @Override
    public void write(zserio.runtime.io.BitStreamWriter out) throws java.io.IOException
    {
        write(out, true);
    }

<#macro choice_write_member member indent packed index>
    <#if member.compoundField??>
        <@compound_check_constraint_field member.compoundField, name, indent/>
        <@compound_write_field member.compoundField, name, indent, packed, index/>
    <#else>
        <#local I>${""?left_pad(indent * 4)}</#local>
        <#lt>${I}// empty
    </#if>
</#macro>
    @Override
    public void write(zserio.runtime.io.BitStreamWriter out, boolean callInitializeOffsets)
            throws java.io.IOException
    {
    <#if fieldList?has_content>
        <#if hasFieldWithOffset>
        final long startBitPosition = out.getBitPosition();

        if (callInitializeOffsets)
        {
            initializeOffsets(startBitPosition);
        }

        </#if>
        <@choice_switch "choice_write_member", 2/>
    </#if>
    }

    @Override
    public void write(zserio.runtime.array.PackingContextNode contextNode,
            zserio.runtime.io.BitStreamWriter out) throws java.io.IOException
    {
    <#if fieldList?has_content>
        <@choice_switch "choice_write_member", 2, true/>
    </#if>
    }
</#if>
<#list fieldList as field>
    <@define_field_helper_classes name, field/>
</#list>

    <@compound_parameter_members compoundParametersData/>
    private java.lang.Object objectChoice;
}
