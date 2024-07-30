<#include "FileHeader.inc.ftl">
<#include "CompoundConstructor.inc.ftl">
<#include "CompoundParameter.inc.ftl">
<#include "CompoundField.inc.ftl">
<#include "CompoundFunction.inc.ftl">
<#include "TypeInfo.inc.ftl">
<#include "Reflectable.inc.ftl">
<@file_header generatorDescription/>

#include <zserio/StringConvertUtil.h>
#include <zserio/CppRuntimeException.h>
#include <zserio/HashCodeUtil.h>
#include <zserio/BitPositionUtil.h>
#include <zserio/BitSizeOfCalculator.h>
#include <zserio/BitFieldUtil.h>
<#if withTypeInfoCode>
#include <zserio/TypeInfo.h>
    <#if withReflectionCode>
<@type_includes types.reflectableFactory/>
    </#if>
</#if>
<#if has_field_with_constraint(fieldList)>
#include <zserio/ConstraintException.h>
</#if>
<#if (withReflectionCode && has_non_simple_parameter(compoundParametersData))>
#include <functional>
</#if>
<@system_includes cppSystemIncludes/>

<@user_include package.path, "${name}.h"/>
<@user_includes cppUserIncludes, false/>
<@namespace_begin package.path/>

<#if withWriterCode>
<#macro empty_constructor_field_initialization>
        m_objectChoice(allocator)
</#macro>
<#assign emptyConstructorInitMacroName><#if fieldList?has_content>empty_constructor_field_initialization</#if></#assign>
    <@compound_constructor_definition compoundConstructorsData emptyConstructorInitMacroName/>

</#if>
<#macro read_constructor_field_initialization packed>
        m_objectChoice(readObject(<#if packed>context, </#if>in, allocator), allocator)
</#macro>
<#assign readConstructorInitMacroName><#if fieldList?has_content>read_constructor_field_initialization</#if></#assign>
<@compound_read_constructor_definition compoundConstructorsData, readConstructorInitMacroName/>
<#if isPackable && usedInPackedArray>

<@compound_read_constructor_definition compoundConstructorsData, readConstructorInitMacroName, true/>
</#if>

<#if needs_compound_initialization(compoundConstructorsData) || has_field_with_initialization(fieldList)>
<@compound_copy_constructor_definition compoundConstructorsData/>

<@compound_assignment_operator_definition compoundConstructorsData/>

<@compound_move_constructor_definition compoundConstructorsData/>

<@compound_move_assignment_operator_definition compoundConstructorsData/>

</#if>
<#if needs_compound_initialization(compoundConstructorsData)>
<@compound_copy_constructor_no_init_definition compoundConstructorsData/>

<@compound_assignment_no_init_definition compoundConstructorsData/>

<@compound_move_constructor_no_init_definition compoundConstructorsData/>

<@compound_move_assignment_no_init_definition compoundConstructorsData/>

</#if>
<@compound_allocator_propagating_copy_constructor_definition compoundConstructorsData/>

<#if needs_compound_initialization(compoundConstructorsData)>
<@compound_allocator_propagating_copy_constructor_no_init_definition compoundConstructorsData/>

</#if>
<#macro choice_selector_condition expressionList>
    <#if expressionList?size == 1>
        selector == (${expressionList?first})<#t>
    <#else>
        <#list expressionList as expression>
        (selector == (${expression}))<#if expression?has_next> || </#if><#t>
        </#list>
    </#if>
</#macro>
<#macro choice_no_match name indent>
    <#local I>${""?left_pad(indent * 4)}</#local>
${I}throw ::zserio::CppRuntimeException("No match in choice ${name}!");
</#macro>
<#macro choice_switch memberActionMacroName noMatchMacroName selectorExpression indent packed=false>
    <#local I>${""?left_pad(indent * 4)}</#local>
    <#if canUseNativeSwitch>
${I}switch (${selectorExpression})
${I}{
        <#list caseMemberList as caseMember>
            <#list caseMember.expressionList as expression>
${I}case ${expression}:
            </#list>
        <@.vars[memberActionMacroName] caseMember, packed, indent+1/>
        </#list>
        <#if !isDefaultUnreachable>
${I}default:
            <#if defaultMember??>
        <@.vars[memberActionMacroName] defaultMember, packed, indent+1/>
            <#else>
        <@.vars[noMatchMacroName] name, indent+1/>
            </#if>
        </#if>
${I}}
    <#else>
${I}const auto selector = ${selectorExpression};

        <#list caseMemberList as caseMember>
            <#if caseMember?has_next || !isDefaultUnreachable>
${I}<#if caseMember?index != 0>else </#if>if (<@choice_selector_condition caseMember.expressionList/>)
            <#else>
${I}else
            </#if>
${I}{
        <@.vars[memberActionMacroName] caseMember, packed, indent+1/>
${I}}
        </#list>
        <#if !isDefaultUnreachable>
${I}else
${I}{
            <#if defaultMember??>
        <@.vars[memberActionMacroName] defaultMember, packed, indent+1/>
            <#else>
        <@.vars[noMatchMacroName] name, indent+1/>
            </#if>
${I}}
        </#if>
    </#if>
</#macro>
<#if withTypeInfoCode>
const ${types.typeInfo.name}& ${name}::typeInfo()
{
    <@template_info_template_name_var "templateName", templateInstantiation!/>
    <@template_info_template_arguments_var "templateArguments", templateInstantiation!/>

    <#list fieldList as field>
    <@field_info_recursive_type_info_var field/>
    <@field_info_type_arguments_var field/>
    </#list>
    <@field_info_array_var "fields", fieldList/>

    <@parameter_info_array_var "parameters", compoundParametersData.list/>

    <@function_info_array_var "functions", compoundFunctionsData.list/>

    <#list caseMemberList as caseMember>
    <@case_info_case_expressions_var caseMember caseMember?index/>
    </#list>
    <@case_info_array_var "cases" caseMemberList defaultMember!/>

    static const ::zserio::ChoiceTypeInfo<allocator_type> typeInfo = {
        ::zserio::makeStringView("${schemaTypeName}"),
    <#if withWriterCode && withReflectionCode>
        [](const allocator_type& allocator) -> ${types.reflectablePtr.name}
        {
            return std::allocate_shared<::zserio::ReflectableOwner<${name}>>(allocator, allocator);
        },
    <#else>
        nullptr,
    </#if>
        templateName, templateArguments,
        fields, parameters, functions, ::zserio::makeStringView("${selectorExpression}"), cases
    };

    return typeInfo;
}

    <#if withReflectionCode>
<#macro choice_get_choice member packed indent>
    <#local I>${""?left_pad(indent * 4)}</#local>
    <#if member.compoundField??>
${I}return ::zserio::makeStringView("${member.compoundField.name}");
    <#else>
${I}return {};
    </#if>
</#macro>
<#macro choice_get_choice_no_match name indent>
    <#local I>${""?left_pad(indent * 4)}</#local>
${I}return {};
</#macro>
<#macro choice_reflectable isConst>
<#if isConst>${types.reflectableConstPtr.name}<#else>${types.reflectablePtr.name}</#if> ${name}::reflectable(<#rt>
        <#lt>const allocator_type& allocator)<#if isConst> const</#if>
{
    class Reflectable : public ::zserio::Reflectable<#if isConst>Const</#if>AllocatorHolderBase<allocator_type>
    {
    public:
    <#if isConst>
        using ::zserio::ReflectableConstAllocatorHolderBase<allocator_type>::getField;
        using ::zserio::ReflectableConstAllocatorHolderBase<allocator_type>::getParameter;
        using ::zserio::ReflectableConstAllocatorHolderBase<allocator_type>::callFunction;
        using ::zserio::ReflectableConstAllocatorHolderBase<allocator_type>::getAnyValue;

    </#if>
        explicit Reflectable(<#if isConst>const </#if>${fullName}& object, const allocator_type& alloc) :
                ::zserio::Reflectable<#if isConst>Const</#if>AllocatorHolderBase<allocator_type>(<#rt>
                        <#lt>${fullName}::typeInfo(), alloc),
                m_object(object)
        {}
    <#if !isConst>

        <@reflectable_initialize_children needsChildrenInitialization/>
        <#if needs_compound_initialization(compoundConstructorsData)>

        <@reflectable_initialize name compoundParametersData.list/>
        </#if>

        size_t initializeOffsets(size_t bitPosition) override
        {
            return m_object.initializeOffsets(bitPosition);
        }
    </#if>

        size_t bitSizeOf(size_t bitPosition) const override
        {
            return m_object.bitSizeOf(bitPosition);
        }

        void write(::zserio::BitStreamWriter&<#if withWriterCode> writer</#if>) const override
        {
    <#if withWriterCode>
            m_object.write(writer);
    <#else>
            throw ::zserio::CppRuntimeException("Reflectable '${name}': ") <<
                    "Writer code is disabled by -withoutWriterCode zserio option!";
    </#if>
        }
    <#if fieldList?has_content>

        <@reflectable_get_field name, fieldList, true/>
        <#if !isConst>

        <@reflectable_get_field name, fieldList, false/>

        <@reflectable_set_field name, fieldList/>

        <@reflectable_create_field name, fieldList/>
        </#if>
    </#if>
    <#if compoundParametersData.list?has_content>

        <@reflectable_get_parameter name, compoundParametersData.list, true/>
        <#if !isConst>

        <@reflectable_get_parameter name, compoundParametersData.list, false/>
        </#if>
    </#if>
    <#if compoundFunctionsData.list?has_content>

        <@reflectable_call_function name, compoundFunctionsData.list, true/>
        <#if !isConst>

        <@reflectable_call_function name, compoundFunctionsData.list, false/>
        </#if>
    </#if>

        ::zserio::StringView getChoice() const override
        {
    <#if fieldList?has_content>
            <@choice_switch "choice_get_choice", "choice_get_choice_no_match", objectIndirectSelectorExpression, 3/>
    <#else>
            return {};
    </#if>
        }

        ${types.anyHolder.name} getAnyValue(const allocator_type& alloc) const override
        {
            return ${types.anyHolder.name}(::std::cref(m_object), alloc);
        }
    <#if !isConst>

        ${types.anyHolder.name} getAnyValue(const allocator_type& alloc) override
        {
            return ${types.anyHolder.name}(::std::ref(m_object), alloc);
        }
    </#if>

    <#if withBitPositionCode>
        size_t bitPosition() const override
        {
            return m_object.bitPosition();
        }
    </#if>

    private:
        <#if isConst>const </#if>${fullName}& m_object;
    };

    return std::allocate_shared<Reflectable>(allocator, *this, allocator);
}
</#macro>
<@choice_reflectable true/>

        <#if withWriterCode>
<@choice_reflectable false/>
        </#if>
    </#if>
</#if>
<#if needs_compound_initialization(compoundConstructorsData)>
<@compound_initialize_definition compoundConstructorsData, needsChildrenInitialization/>

</#if>
<#macro choice_initialize_children_member member packed indent>
    <#local I>${""?left_pad(indent * 4)}</#local>
    <#if member.compoundField??>
    <@compound_initialize_children_field member.compoundField, indent/>
    <#else>
${I}// empty
    </#if>
    <#if canUseNativeSwitch>
${I}break;
    </#if>
</#macro>
<#if needsChildrenInitialization>
void ${name}::initializeChildren()
{
    <#if fieldList?has_content>
    <@choice_switch "choice_initialize_children_member", "choice_no_match", selectorExpression, 1/>
    </#if>
    <@compound_initialize_children_epilog_definition compoundConstructorsData/>
}

</#if>
<@compound_parameter_accessors_definition name, compoundParametersData/>
<#list fieldList as field>
    <#if needs_field_getter(field)>
<@field_raw_cpp_type_name field/>& ${name}::${field.getterName}()
{
    return m_objectChoice.get<<@field_cpp_type_name field/>>()<#if field.array??>.getRawArray()</#if>;
}

    </#if>
<@field_raw_cpp_argument_type_name field/> ${name}::${field.getterName}() const
{
    return m_objectChoice.get<<@field_cpp_type_name field/>>()<#if field.array??>.getRawArray()</#if>;
}

    <#if needs_field_setter(field)>
void ${name}::${field.setterName}(<@field_raw_cpp_argument_type_name field/> <@field_argument_name field/>)
{
    m_objectChoice = <@compound_setter_field_value field/>;
}

    </#if>
    <#if needs_field_rvalue_setter(field)>
void ${name}::${field.setterName}(<@field_raw_cpp_type_name field/>&& <@field_argument_name field/>)
{
    m_objectChoice = <@compound_setter_field_rvalue field/>;
}

    </#if>
</#list>
<@compound_functions_definition name, compoundFunctionsData/>
<#macro choice_tag_no_match name indent>
    <#local I>${""?left_pad(indent * 4)}</#local>
${I}return UNDEFINED_CHOICE;
</#macro>
<#macro choice_tag_member member packed indent>
    <#local I>${""?left_pad(indent * 4)}</#local>
    <#if member.compoundField??>
${I}return <@choice_tag_name member.compoundField/>;
    <#else>
${I}return UNDEFINED_CHOICE;
    </#if>
</#macro>
${name}::ChoiceTag ${name}::choiceTag() const
{
<#if fieldList?has_content>
    <@choice_switch "choice_tag_member", "choice_tag_no_match", selectorExpression, 1, true/>
<#else>
    return UNDEFINED_CHOICE;
</#if>
}
<#if isPackable && usedInPackedArray>

<#macro init_packing_context_member member packed indent>
    <#local I>${""?left_pad(indent * 4)}</#local>
    <#if member.compoundField??>
    <@compound_init_packing_context_field member.compoundField, indent/>
    <#else>
${I}// empty
    </#if>
    <#if canUseNativeSwitch>
${I}break;
    </#if>
</#macro>
void ${name}::initPackingContext(${name}::ZserioPackingContext&<#if uses_packing_context(fieldList)> context</#if>) const
{
    <@choice_switch "init_packing_context_member", "choice_no_match", selectorExpression, 1, true/>
}
</#if>

<#macro choice_bitsizeof_member member packed indent>
    <#local I>${""?left_pad(indent * 4)}</#local>
    <#if member.compoundField??>
    <@compound_bitsizeof_field member.compoundField, indent, packed/>
    <#else>
${I}// empty
    </#if>
    <#if canUseNativeSwitch>
${I}break;
    </#if>
</#macro>
size_t ${name}::bitSizeOf(size_t<#if fieldList?has_content> bitPosition</#if>) const
{
<#if fieldList?has_content>
    size_t endBitPosition = bitPosition;

    <@choice_switch "choice_bitsizeof_member", "choice_no_match", selectorExpression, 1/>

    return endBitPosition - bitPosition;
<#else>
    return 0;
</#if>
}
<#if isPackable && usedInPackedArray>

size_t ${name}::bitSizeOf(${name}::ZserioPackingContext&<#if uses_packing_context(fieldList)> context</#if>, <#rt>
        <#lt>size_t bitPosition) const
{
    size_t endBitPosition = bitPosition;

    <@choice_switch "choice_bitsizeof_member", "choice_no_match", selectorExpression, 1, true/>

    return endBitPosition - bitPosition;
}
</#if>
<#if withWriterCode>

<#macro choice_initialize_offsets_member member packed indent>
    <#local I>${""?left_pad(indent * 4)}</#local>
    <#if member.compoundField??>
    <@compound_initialize_offsets_field member.compoundField, indent, packed/>
    <#else>
${I}// empty
    </#if>
    <#if canUseNativeSwitch>
${I}break;
    </#if>
</#macro>
size_t ${name}::initializeOffsets(size_t bitPosition)
{
    <#if fieldList?has_content>
    size_t endBitPosition = bitPosition;

    <@choice_switch "choice_initialize_offsets_member", "choice_no_match", selectorExpression, 1/>

    return endBitPosition;
    <#else>
    return bitPosition;
    </#if>
}
    <#if isPackable && usedInPackedArray>

size_t ${name}::initializeOffsets(${name}::ZserioPackingContext&<#if uses_packing_context(fieldList)> context</#if>, <#rt>
        <#lt>size_t bitPosition)
{
    size_t endBitPosition = bitPosition;

    <@choice_switch "choice_initialize_offsets_member", "choice_no_match", selectorExpression, 1, true/>

    return endBitPosition;
}
    </#if>
</#if>

<#macro choice_compare_member member packed indent>
    <#local I>${""?left_pad(indent * 4)}</#local>
    <#if member.compoundField??>
${I}return (!m_objectChoice.hasValue() && !other.m_objectChoice.hasValue()) ||
${I}        (m_objectChoice.hasValue() && other.m_objectChoice.hasValue() &&
${I}        m_objectChoice.get<<@field_cpp_type_name member.compoundField/>>() == <#rt>
            <#lt>other.m_objectChoice.get<<@field_cpp_type_name member.compoundField/>>());
    <#else>
${I}return true; // empty
    </#if>
</#macro>
bool ${name}::operator==(const ${name}& other) const
{
    if (this == &other)
    {
        return true;
    }

    <@compound_parameter_comparison_with_any_holder compoundParametersData/>
    <#if fieldList?has_content>
    <@choice_switch "choice_compare_member", "choice_no_match", selectorExpression, 1/>
    <#else>
    return true;
    </#if>
}

<#macro choice_less_than_member member packed indent>
    <#local I>${""?left_pad(indent * 4)}</#local>
    <#if member.compoundField??>
        <#local lhs>m_objectChoice.get<<@field_cpp_type_name member.compoundField/>>()</#local>
        <#local rhs>other.m_objectChoice.get<<@field_cpp_type_name member.compoundField/>>()</#local>
${I}if (m_objectChoice.hasValue() && other.m_objectChoice.hasValue())
${I}{
${I}    return <@compound_field_less_than_compare member.compoundField, lhs, rhs/>;
${I}}
${I}else
${I}{
${I}    return !m_objectChoice.hasValue() && other.m_objectChoice.hasValue();
${I}}
    <#else>
${I}return false;
    </#if>
</#macro>
bool ${name}::operator<(const ${name}&<#if compoundParametersData.list?has_content || fieldList?has_content> other</#if>) const
{
    <@compound_parameter_less_than compoundParametersData, 1/>
    <#if fieldList?has_content>
    <@choice_switch "choice_less_than_member", "choice_no_match", selectorExpression, 1/>
    <#else>
    return false;
    </#if>
}

<#macro choice_hash_code_no_match name indent>
    <#local I>${""?left_pad(indent * 4)}</#local>
    <#if canUseNativeSwitch>
${I}break;
    </#if>
</#macro>
<#macro choice_hash_code_member member packed indent>
    <#local I>${""?left_pad(indent * 4)}</#local>
    <#if member.compoundField??>
${I}result = ::zserio::calcHashCode(result, m_objectChoice.get<<@field_cpp_type_name member.compoundField/>>());
    <#else>
${I}// empty
    </#if>
    <#if canUseNativeSwitch>
${I}break;
    </#if>
</#macro>
uint32_t ${name}::hashCode() const
{
    uint32_t result = ::zserio::HASH_SEED;

    <@compound_parameter_hash_code compoundParametersData/>
<#if fieldList?has_content>
    if (m_objectChoice.hasValue())
    {
        <@choice_switch "choice_hash_code_member", "choice_hash_code_no_match", selectorExpression, 2/>
    }
</#if>

    return result;
}
<#if withWriterCode>

<#macro choice_write_member member packed indent>
    <#local I>${""?left_pad(indent * 4)}</#local>
    <#if member.compoundField??>
    <@compound_write_field member.compoundField, name, indent, packed/>
    <#else>
${I}// empty
    </#if>
    <#if canUseNativeSwitch>
${I}break;
    </#if>
</#macro>
void ${name}::write(::zserio::BitStreamWriter&<#if fieldList?has_content> out</#if>) const
{
    <#if fieldList?has_content>
    <@choice_switch "choice_write_member", "choice_no_match", selectorExpression, 1/>
    </#if>
}
    <#if isPackable && usedInPackedArray>

void ${name}::write(${name}::ZserioPackingContext&<#if uses_packing_context(fieldList)> context</#if>, <#rt>
        <#lt>::zserio::BitStreamWriter& out) const
{
    <@choice_switch "choice_write_member", "choice_no_match", selectorExpression, 1, true/>
}
    </#if>
</#if>
<#if withBitPositionCode>
size_t ${name}::bitPosition() const
{
    return m_bitPosition;
}
</#if>
<#if fieldList?has_content>

<@inner_classes_definition name, fieldList/>
<#macro choice_read_member member packed indent>
    <#local I>${""?left_pad(indent * 4)}</#local>
    <#if member.compoundField??>
        <#if needs_field_read_local_variable(member.compoundField)>
${I}{
        <@compound_read_field member.compoundField, name, indent+1, packed/>
${I}}
        <#else>
    <@compound_read_field member.compoundField, name, indent, packed/>
        </#if>
    <#else>
${I}return ${types.anyHolder.name}(allocator);
    </#if>
</#macro>
${types.anyHolder.name} ${name}::readObject(::zserio::BitStreamReader& in, const allocator_type& allocator)
{
    <@choice_switch "choice_read_member", "choice_no_match", selectorExpression, 1/>
}
<#if isPackable && usedInPackedArray>

${types.anyHolder.name} ${name}::readObject(${name}::ZserioPackingContext&<#if uses_packing_context(fieldList)> context</#if>,
        ::zserio::BitStreamReader& in, const allocator_type& allocator)
{
    <@choice_switch "choice_read_member", "choice_no_match", selectorExpression, 1, true/>
}
</#if>

<#macro choice_copy_object member packed indent>
    <#local I>${""?left_pad(indent * 4)}</#local>
    <#if member.compoundField??>
${I}return ::zserio::allocatorPropagatingCopy<<@field_cpp_type_name member.compoundField/>>(<#rt>
        <#if has_field_no_init_tag(member.compoundField)>
        ::zserio::NoInit, <#t>
        </#if>
        <#lt>m_objectChoice, allocator);
    <#else>
${I}return ${types.anyHolder.name}(allocator);
    </#if>
</#macro>
${types.anyHolder.name} ${name}::copyObject(const allocator_type& allocator) const
{
    <@choice_switch "choice_copy_object", "choice_no_match", selectorExpression, 1/>
}
</#if>
<@namespace_end package.path/>
