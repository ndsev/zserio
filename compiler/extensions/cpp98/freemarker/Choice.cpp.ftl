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
<#if withInspectorCode>
#include <zserio/inspector/BlobInspectorTreeUtil.h>
</#if>
<@system_includes cppSystemIncludes, true/>

#include "<@include_path package.path, "${name}.h"/>"
<#if withInspectorCode>
#include "<@include_path rootPackage.path, "InspectorZserioTypeNames.h"/>"
#include "<@include_path rootPackage.path, "InspectorZserioNames.h"/>"
</#if>
<@user_includes cppUserIncludes, false/>

<@namespace_begin package.path/>

<#assign hasAnonymousNamespace=false/>
<#list fieldList as field>
    <#if field.array?? && field.array.requiresElementFactory>
        <#if !hasAnonymousNamespace>
            <#assign hasAnonymousNamespace=true/>
<@anonymous_namespace_begin/>

        </#if>
<@define_element_factory name, field/>

        <#if field.array.elementCompound??>
            <#if needs_compound_field_initialization(field.array.elementCompound)>
<@define_element_initializer name, field/>

            <#elseif field.array.elementCompound.needsChildrenInitialization>
<@define_element_children_initializer name, field/>

            </#if>
        </#if>
    </#if>
</#list>
<#if hasAnonymousNamespace>
<@anonymous_namespace_end/>

</#if>
<#if withWriterCode>
${name}::${name}()<#rt>
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

<#if withInspectorCode>
<@compound_read_tree_constructor_definition compoundConstructorsData/>

</#if>
<#if needs_compound_initialization(compoundConstructorsData) || has_field_with_initialization(fieldList)>
<@compound_copy_constructor_definition compoundConstructorsData/>

<@compound_assignment_operator_definition compoundConstructorsData/>

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
<#macro choice_switch memberActionMacroName>
    <#if !isSelectorExpressionBoolean>
    switch (${selectorExpression})
    {
        <#list caseMemberList as caseMember>
            <#list caseMember.expressionList as expression>
    case ${expression}:
            </#list>
        <@.vars[memberActionMacroName] caseMember/>
        break;
        </#list>
        <#if !isDefaultUnreachable>
    default:
            <#if defaultMember??>
        <@.vars[memberActionMacroName] defaultMember/>
        break;
            <#else>
        throw zserio::CppRuntimeException("No match in choice ${name}!");
            </#if>
        </#if>
    }
    <#else>
    const bool _selector = ${selectorExpression};

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

</#list>
<@compound_functions_definition name, compoundFunctionsData/>
<#macro choice_bitsizeof_member member>
    <#if member.compoundField??>
        <@compound_bitsizeof_field member.compoundField, 2/>
    <#else>
        // empty
    </#if>
</#macro>
size_t ${name}::bitSizeOf(size_t<#if fieldList?has_content> _bitPosition</#if>) const
{
<#if fieldList?has_content>
    size_t _endBitPosition = _bitPosition;

    <@choice_switch "choice_bitsizeof_member"/>

    return _endBitPosition - _bitPosition;
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
size_t ${name}::initializeOffsets(size_t _bitPosition)
{
    <#if fieldList?has_content>
    size_t _endBitPosition = _bitPosition;

    <@choice_switch "choice_initialize_offsets_member"/>

    return _endBitPosition;
    <#else>
    return _bitPosition;
    </#if>
}
</#if>

bool ${name}::operator==(const ${name}& _other) const
{
    if (this != &_other)
    {
        return
                <@compound_parameter_comparison compoundParametersData, true/>
                (m_objectChoice == _other.m_objectChoice);
    }

    return true;
}

int ${name}::hashCode() const
{
    int _result = zserio::HASH_SEED;

    <@compound_parameter_hash_code compoundParametersData/>
    _result = zserio::calcHashCode(_result, m_objectChoice);

    return _result;
}

<#macro choice_read_member member>
    <#if member.compoundField??>
        <@compound_read_field member.compoundField, name, 2/>
        <@compound_check_constraint_field member.compoundField, name, 2/>
    <#else>
        // empty
    </#if>
</#macro>
void ${name}::read(zserio::BitStreamReader&<#if fieldList?has_content> _in</#if>)
{
<#if fieldList?has_content>
    <@choice_switch "choice_read_member"/>
</#if>
}
<#if withInspectorCode>

<#macro choice_read_tree_member member>
    <#if member.compoundField??>
        {
            const size_t _treeFieldIndex = 0;
            <@compound_read_tree_field member.compoundField, false, name, 3/>
            <@compound_check_constraint_field member.compoundField, name, 3/>
        }
    <#else>
        // empty
    </#if>
</#macro>
void ${name}::read(const zserio::BlobInspectorTree&<#if fieldList?has_content> _tree</#if>)
{
<#if fieldList?has_content>
    <@choice_switch "choice_read_tree_member"/>
</#if>
}
</#if>
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
void ${name}::write(zserio::BitStreamWriter&<#if fieldList?has_content> _out</#if>, <#rt>
        zserio::PreWriteAction<#if hasPreWriteAction> _preWriteAction</#if>)<#lt>
{
    <#if fieldList?has_content>
        <#if hasPreWriteAction>
    <@compound_pre_write_actions needsRangeCheck, needsChildrenInitialization, hasFieldWithOffset/>

        </#if>
    <@choice_switch "choice_write_member"/>
    </#if>
}
</#if>
<#if withInspectorCode>

<#macro choice_write_tree_member member>
    <#if member.compoundField??>
        {
            <@compound_check_constraint_field member.compoundField, name, 3/>
            <@compound_write_tree_field member.compoundField, name, rootPackage.name, 3/>
        }
    <#else>
        // empty
    </#if>
</#macro>
void ${name}::write(zserio::BitStreamWriter&<#if fieldList?has_content> _out</#if>, <#rt>
        zserio::BlobInspectorTree&<#if fieldList?has_content || compoundFunctionsData.list?has_content> _tree</#if>,<#lt>
        zserio::PreWriteAction<#if hasPreWriteAction> _preWriteAction</#if>)
{
    <#if fieldList?has_content>
        <#if hasPreWriteAction>
    <@compound_pre_write_actions needsRangeCheck, needsChildrenInitialization, hasFieldWithOffset/>

        </#if>
    <@choice_switch "choice_write_tree_member"/>
    </#if>
    <@compound_functions_write_tree rootPackage.name, compoundFunctionsData/>
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
