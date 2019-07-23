<#include "FileHeader.inc.ftl">
<#include "CompoundConstructor.inc.ftl">
<#include "CompoundParameter.inc.ftl">
<#include "CompoundField.inc.ftl">
<#include "CompoundFunction.inc.ftl">
<@file_header generatorDescription/>

#include <zserio/StringConvertUtil.h>
#include <zserio/CppRuntimeException.h>
#include <zserio/HashCodeUtil.h>
#include <zserio/BitPositionUtil.h>
#include <zserio/BitSizeOfCalculator.h>
#include <zserio/BitFieldUtil.h>
<#list fieldList as field>
    <#if field.constraint??>
#include <zserio/ConstraintException.h>
        <#break>
    </#if>
</#list>
<@system_includes cppSystemIncludes, false/>

#include "<@include_path package.path, "${name}.h"/>"
<@user_includes cppUserIncludes, false/>

<@namespace_begin package.path/>

<@define_inner_classes fieldList/>
<#if withWriterCode>
${name}::${name}() noexcept<#rt>
    <#assign constructorMembersInitialization><@compound_constructor_members_initialization compoundConstructorsData/></#assign>
    <#if constructorMembersInitialization?has_content>
        <#lt> :
        ${constructorMembersInitialization}
    <#else>

    </#if>
{
}

</#if>
<@compound_read_constructor_definition compoundConstructorsData/>

<#if needs_compound_initialization(compoundConstructorsData) || has_field_with_initialization(fieldList)>
<@compound_copy_constructor_definition compoundConstructorsData/>

<@compound_assignment_operator_definition compoundConstructorsData/>

<@compound_move_constructor_definition compoundConstructorsData/>

<@compound_move_assignment_constructor_definition compoundConstructorsData/>

</#if>
<#if needs_compound_initialization(compoundConstructorsData)>
<@compound_initialize_definition compoundConstructorsData, needsChildrenInitialization/>

</#if>
<#macro choice_selector_condition expressionList>
    <#if expressionList?size == 1>
        _selector == ${expressionList?first}<#t>
    <#else>
        <#list expressionList as expression>
        (_selector == ${expression})<#if expression_has_next> || </#if><#t>
        </#list>
    </#if>
</#macro>
<#macro choice_switch memberActionMacroName needsBreak=true>
    <#local useSwitch = selectorExpressionTypeName != "bool"/>
    <#if useSwitch>
    switch (${selectorExpression})
    {
        <#list caseMemberList as caseMember>
            <#list caseMember.expressionList as expression>
    case ${expression}:
            </#list>
        <@.vars[memberActionMacroName] caseMember/>
            <#if needsBreak>
        break;
            </#if>
        </#list>
        <#if !isDefaultUnreachable>
    default:
            <#if defaultMember??>
        <@.vars[memberActionMacroName] defaultMember/>
                <#if needsBreak>
        break;
                </#if>
            <#else>
        throw zserio::CppRuntimeException("No match in choice ${name}!");
            </#if>
        </#if>
    }
    <#else>
    const ${selectorExpressionTypeName} _selector = ${selectorExpression};

        <#list caseMemberList as caseMember>
            <#if caseMember_has_next || !isDefaultUnreachable>
    <#if caseMember_index != 0>else </#if>if (<@choice_selector_condition caseMember.expressionList/>)
            <#else>
    else
            </#if>
    {
        <@.vars[memberActionMacroName] caseMember/>
    }
        </#list>
        <#if !isDefaultUnreachable>
    else
    {
            <#if defaultMember??>
        <@.vars[memberActionMacroName] defaultMember/>
            <#else>
        throw zserio::CppRuntimeException("No match in choice ${name}!");
            </#if>
    }
        </#if>
    </#if>
</#macro>
<#macro choice_initialize_children_member member>
    <#if member.compoundField??>
        <@compound_initialize_children_field member.compoundField, name, 2/>
    <#else>
        // empty
    </#if>
</#macro>
<#if needsChildrenInitialization>
void ${name}::initializeChildren()
{
    <#if fieldList?has_content>
    <@choice_switch "choice_initialize_children_member"/>
    </#if>
    <@compound_initialize_children_epilog_definition compoundConstructorsData/>
}

</#if>
<@compound_parameter_accessors_definition name, compoundParametersData/>
<#list fieldList as field>
<@compound_field_getter_definition field name "compound_return_field"/>
<@compound_field_const_getter_definition field name "compound_return_field"/>
<@compound_field_setter_definition field name "compound_set_field"/>
<@compound_field_rvalue_setter_definition field name "compound_set_field"/>
</#list>
<@compound_functions_definition name, compoundFunctionsData/>
<#macro choice_bitsizeof_member member>
    <#if member.compoundField??>
        <@compound_bitsizeof_field member.compoundField, 2/>
    <#else>
        // empty
    </#if>
</#macro>
size_t ${name}::bitSizeOf(size_t<#if fieldList?has_content> bitPosition</#if>) const
{
<#if fieldList?has_content>
    size_t endBitPosition = bitPosition;

    <@choice_switch "choice_bitsizeof_member"/>

    return endBitPosition - bitPosition;
<#else>
    return 0;
</#if>
}
<#if withWriterCode>

<#macro choice_initialize_offsets_member member>
    <#if member.compoundField??>
        <@compound_initialize_offsets_field member.compoundField, 2/>
    <#else>
        // empty
    </#if>
</#macro>
size_t ${name}::initializeOffsets(size_t bitPosition)
{
    <#if fieldList?has_content>
    size_t endBitPosition = bitPosition;

    <@choice_switch "choice_initialize_offsets_member"/>

    return endBitPosition;
    <#else>
    return bitPosition;
    </#if>
}
</#if>

<#macro choice_compare_member member>
    <#if member.compoundField??>
        return <@compound_get_field member.compoundField/> == other.<@compound_get_field member.compoundField/>;
    <#else>
        return true; // empty
    </#if>
</#macro>
bool ${name}::operator==(const ${name}& other) const
{
    if (this == &other)
        return true;
        
    <@compound_parameter_comparison_with_any_holder compoundParametersData/>
    <#if fieldList?has_content>
    <@choice_switch "choice_compare_member" false/>
    <#else>
    return true;
    </#if>
}

<#-- TODO: Check if it's ok that it fires exception for uninitialized choice! -->
<#macro choice_hash_code_member member>
    <#if member.compoundField??>
        result = zserio::calcHashCode(result, <@compound_get_field member.compoundField/>);
    <#else>
        // empty
    </#if>
</#macro>
int ${name}::hashCode() const
{
    int result = zserio::HASH_SEED;

    <@compound_parameter_hash_code compoundParametersData/>
    <#if fieldList?has_content>
    <@choice_switch "choice_hash_code_member"/>
    </#if>

    return result;
}

<#macro choice_read_member member>
    <#if member.compoundField??>
        <@compound_read_field member.compoundField, name, 2/>
        <@compound_check_constraint_field member.compoundField, name, 2/>
    <#else>
        // empty
    </#if>
</#macro>
void ${name}::read(zserio::BitStreamReader&<#if fieldList?has_content> in</#if>)
{
<#if fieldList?has_content>
    <@choice_switch "choice_read_member"/>
</#if>
}
<#assign needsRangeCheck=withRangeCheckCode && has_field_with_range_check(fieldList)/>
<#if withWriterCode>

<#macro choice_write_member member>
    <#if member.compoundField??>
        <@compound_check_constraint_field member.compoundField, name, 2/>
        <@compound_write_field member.compoundField, name, 2/>
    <#else>
        // empty
    </#if>
</#macro>
<#assign hasPreWriteAction=needsRangeCheck || needsChildrenInitialization || hasFieldWithOffset/>
void ${name}::write(zserio::BitStreamWriter&<#if fieldList?has_content> out</#if>, <#rt>
        zserio::PreWriteAction<#if hasPreWriteAction> preWriteAction</#if>)<#lt>
{
    <#if fieldList?has_content>
        <#if hasPreWriteAction>
    <@compound_pre_write_actions needsRangeCheck, needsChildrenInitialization, hasFieldWithOffset/>

        </#if>
    <@choice_switch "choice_write_member"/>
    </#if>
}
</#if>
<#macro choice_check_ranges_member member>
    <#if member.compoundField??>
        <#if needs_field_range_check(member.compoundField)>
        {
            <@compound_check_range_field member.compoundField, name, 3/>
        }
        </#if>
    <#else>
        // empty
    </#if>
</#macro>
<#if needsRangeCheck>

void ${name}::checkRanges()
{
    <#if fieldList?has_content>
    <@choice_switch "choice_check_ranges_member"/>
    </#if>
}
</#if>

<@namespace_end package.path/>
