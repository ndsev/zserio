<#include "FileHeader.inc.ftl">
<#include "CompoundConstructor.inc.ftl">
<#include "CompoundParameter.inc.ftl">
<#include "CompoundFunction.inc.ftl">
<#include "CompoundField.inc.ftl">
<#include "RangeCheck.inc.ftl">
<@standard_header generatorDescription, packageName/>

public class ${name} implements <#if withWriterCode>zserio.runtime.io.InitializeOffsetsWriter, </#if>zserio.runtime.SizeOf
{
    <@compound_constructors compoundConstructorsData/>
    @Override
    public int bitSizeOf() throws zserio.runtime.ZserioError
    {
        return bitSizeOf(0);
    }

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
<#macro choice_switch memberActionMacroName indent>
    <#local I>${""?left_pad(indent * 4)}</#local>
    <#if isSwitchAllowed>
${I}switch (${selectorExpression})
${I}{
        <#list caseMemberList as caseMember>
            <#list caseMember.caseList as case>
${I}case ${case.expressionForCase}:
            </#list>
        <@.vars[memberActionMacroName] caseMember, indent + 1/>
${I}    break;
        </#list>
        <#if !isDefaultUnreachable>
${I}default:
            <#if defaultMember??>
        <@.vars[memberActionMacroName] defaultMember, indent + 1/>
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
        <@.vars[memberActionMacroName] caseMember, indent + 1/>
${I}}
        </#list>
        <#if !isDefaultUnreachable>
${I}else
${I}{
            <#if defaultMember??>
        <@.vars[memberActionMacroName] defaultMember, indent + 1/>
            <#else>
${I}    throw new zserio.runtime.ZserioError("No match in choice ${name}: " + ${selectorExpression} + "!");
            </#if>
${I}}
        </#if>
    </#if>
</#macro>
<#macro choice_bitsizeof_member member indent>
    <#if member.compoundField??>
        <@compound_bitsizeof_field member.compoundField, indent/>
    <#else>
        <#local I>${""?left_pad(indent * 4)}</#local>
        <#lt>${I}// empty
    </#if>
</#macro>
    @Override
    public int bitSizeOf(long bitPosition) throws zserio.runtime.ZserioError
    {
<#if fieldList?has_content>
        long endBitPosition = bitPosition;

        <@choice_switch "choice_bitsizeof_member", 2/>

        return (int)(endBitPosition - bitPosition);
<#else>
        return 0;
</#if>
    }

<@compound_parameter_accessors compoundParametersData/>
<#list fieldList as field>
    <#if field.isObjectArray>@java.lang.SuppressWarnings("unchecked")</#if>
    public ${field.javaTypeName} ${field.getterName}()
    {
        return (${field.javaNullableTypeName}) this.objectChoice;
    }

    <#if withWriterCode>
    public void ${field.setterName}(${field.javaTypeName} <@field_argument_name field/>)
    {
        <@range_check field.rangeCheckData, name/>
        this.objectChoice = <@field_argument_name field/>;
    }

        <#if field.array?? && field.array.generateListSetter>
    public void ${field.setterName}(java.util.List<${field.array.elementJavaTypeName}> <@field_argument_name field/>)
    {
        ${field.setterName}(new ${field.javaTypeName}(<@field_argument_name field/>));
    }

        </#if>
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
                        (this.objectChoice == null && that.objectChoice == null) ||
                        (this.objectChoice != null && this.objectChoice.equals(that.objectChoice))
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
                ((this.objectChoice == null) ? 0 : this.objectChoice.hashCode());

        return result;
    }

<#macro choice_read_member member indent>
    <#if member.compoundField??>
        <@compound_read_field member.compoundField, name, indent/>
        <@compound_check_constraint_field member.compoundField, name, indent/>
    <#else>
        <#local I>${""?left_pad(indent * 4)}</#local>
        <#lt>${I}// empty
    </#if>
</#macro>
    public void read(final zserio.runtime.io.BitStreamReader in)
            throws java.io.IOException, zserio.runtime.ZserioError
    {
<#if fieldList?has_content>
        <@choice_switch "choice_read_member", 2/>
</#if>
    }
<#if withWriterCode>

<#macro choice_initialize_offsets_member member indent>
    <#local I>${""?left_pad(indent * 4)}</#local>
    <#if member.compoundField??>
        <#if isSwitchAllowed>
${I}{
        <@compound_field_initialize_offsets member.compoundField, indent + 1/>
${I}}
        <#else>
    <@compound_field_initialize_offsets member.compoundField, indent/>
        </#if>
    <#else>
${I}// empty
    </#if>
</#macro>
    public long initializeOffsets(long bitPosition) throws zserio.runtime.ZserioError
    {
    <#if fieldList?has_content>
        long endBitPosition = bitPosition;

        <@choice_switch "choice_initialize_offsets_member", 2/>

        return endBitPosition;
    <#else>
        return bitPosition;
    </#if>
    }

    public void write(java.io.File file) throws java.io.IOException, zserio.runtime.ZserioError
    {
        zserio.runtime.io.FileBitStreamWriter out = new zserio.runtime.io.FileBitStreamWriter(file);
        write(out);
        out.close();
    }

    @Override
    public void write(zserio.runtime.io.BitStreamWriter out)
            throws java.io.IOException, zserio.runtime.ZserioError
    {
        write(out, true);
    }

<#macro choice_write_member member indent>
    <#if member.compoundField??>
        <@compound_check_constraint_field member.compoundField, name, indent/>
        <@compound_write_field member.compoundField, name, indent/>
    <#else>
        <#local I>${""?left_pad(indent * 4)}</#local>
        <#lt>${I}// empty
    </#if>
</#macro>
    @Override
    public void write(zserio.runtime.io.BitStreamWriter out, boolean callInitializeOffsets)
            throws java.io.IOException, zserio.runtime.ZserioError
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
</#if>
<#list fieldList as field>
    <@define_field_helper_classes name, field/>
</#list>

    <@compound_parameter_members compoundParametersData/>
    private java.lang.Object objectChoice;
}
