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
<#if has_field_with_constraint(fieldList)>
#include <zserio/ConstraintException.h>
</#if>
<@system_includes cppSystemIncludes/>

<@user_include package.path, "${name}.h"/>
<@user_includes cppUserIncludes, false/>
<@namespace_begin package.path/>

<@inner_classes_definition fieldList/>
<#if withWriterCode>
    <@compound_constructor_definition compoundConstructorsData/>

</#if>
<#macro read_constructor_field_initialization>
        m_objectChoice(readObject(in))
</#macro>
<#assign readConstructorInitMacroName><#if fieldList?has_content>read_constructor_field_initialization</#if></#assign>
<@compound_read_constructor_definition compoundConstructorsData, readConstructorInitMacroName/>

<#if needs_compound_initialization(compoundConstructorsData) || has_field_with_initialization(fieldList)>
<@compound_copy_constructor_definition compoundConstructorsData/>

<@compound_assignment_operator_definition compoundConstructorsData/>

<@compound_move_constructor_definition compoundConstructorsData/>

<@compound_move_assignment_operator_definition compoundConstructorsData/>

</#if>
<#if needs_compound_initialization(compoundConstructorsData)>
<@compound_initialize_definition compoundConstructorsData, needsChildrenInitialization/>

</#if>
<#macro choice_selector_condition expressionList>
    <#if expressionList?size == 1>
        selector == ${expressionList?first}<#t>
    <#else>
        <#list expressionList as expression>
        (selector == ${expression})<#if expression?has_next> || </#if><#t>
        </#list>
    </#if>
</#macro>
<#macro choice_switch memberActionMacroName needsBreak=true>
    <#if !isSelectorExpressionBoolean>
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
        throw ::zserio::CppRuntimeException("No match in choice ${name}!");
            </#if>
        </#if>
    }
    <#else>
    const bool selector = ${selectorExpression};

        <#list caseMemberList as caseMember>
            <#if caseMember?has_next || !isDefaultUnreachable>
    <#if caseMember?index != 0>else </#if>if (<@choice_selector_condition caseMember.expressionList/>)
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
        throw ::zserio::CppRuntimeException("No match in choice ${name}!");
            </#if>
    }
        </#if>
    </#if>
</#macro>
<#macro choice_initialize_children_member member>
    <#if member.compoundField??>
        <@compound_initialize_children_field member.compoundField, 2/>
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
    <#if needs_field_getter(field)>
${field.cppTypeName}& ${name}::${field.getterName}()
{
    return m_objectChoice.get<${field.cppTypeName}>();
}

    </#if>
${field.cppArgumentTypeName} ${name}::${field.getterName}() const
{
    return m_objectChoice.get<${field.cppTypeName}>();
}

    <#if needs_field_setter(field)>
void ${name}::${field.setterName}(${field.cppArgumentTypeName} <@field_argument_name field.name/>)
{
    m_objectChoice = <@field_argument_name field.name/>;
}

    </#if>
    <#if needs_field_rvalue_setter(field)>
void ${name}::${field.setterName}(${field.cppTypeName}&& <@field_argument_name field.name/>)
{
    m_objectChoice = ::std::move(<@field_argument_name field.name/>);
}

    </#if>
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
        return (!m_objectChoice.hasValue() && !other.m_objectChoice.hasValue()) ||
                (m_objectChoice.hasValue() && other.m_objectChoice.hasValue() &&
                m_objectChoice.get<${member.compoundField.cppTypeName}>() == <#rt>
                <#lt>other.m_objectChoice.get<${member.compoundField.cppTypeName}>());
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
    <@choice_switch "choice_compare_member", false/>
    <#else>
    return true;
    </#if>
}

<#macro choice_hash_code_member member>
    <#if member.compoundField??>
        if (m_objectChoice.hasValue())
            result = ::zserio::calcHashCode(result, m_objectChoice.get<${member.compoundField.cppTypeName}>());
    <#else>
        // empty
    </#if>
</#macro>
int ${name}::hashCode() const
{
    int result = ::zserio::HASH_SEED;

    <@compound_parameter_hash_code compoundParametersData/>
    <#if fieldList?has_content>
    <@choice_switch "choice_hash_code_member"/>
    </#if>

    return result;
}

void ${name}::read(::zserio::BitStreamReader&<#if fieldList?has_content> in</#if>)
{
<#if fieldList?has_content>
    m_objectChoice = readObject(in);
</#if>
}
<#if withWriterCode>

<#macro choice_write_member member>
    <#if member.compoundField??>
        <@compound_write_field member.compoundField, name, 2/>
    <#else>
        // empty
    </#if>
</#macro>
<#assign hasPreWriteAction=needsChildrenInitialization || hasFieldWithOffset/>
void ${name}::write(::zserio::BitStreamWriter&<#if fieldList?has_content> out</#if>, <#rt>
        ::zserio::PreWriteAction<#if hasPreWriteAction> preWriteAction</#if>)<#lt>
{
    <#if fieldList?has_content>
        <#if hasPreWriteAction>
    <@compound_pre_write_actions needsChildrenInitialization, hasFieldWithOffset/>

        </#if>
    <@choice_switch "choice_write_member"/>
    </#if>
}
</#if>
<#if fieldList?has_content>

<#macro choice_read_member member>
    <#if member.compoundField??>
        <#if needs_field_read_local_variable(member.compoundField)>
        {
            <@compound_read_field member.compoundField, name, 3/>
        }
        <#else>
        <@compound_read_field member.compoundField, name, 2/>
        </#if>
    <#else>
        return ::zserio::AnyHolder();
    </#if>
</#macro>
::zserio::AnyHolder ${name}::readObject(::zserio::BitStreamReader& in)
{
    <@choice_switch "choice_read_member", false/>
}
</#if>
<@namespace_end package.path/>
