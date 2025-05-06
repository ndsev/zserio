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
<@type_includes types.anyHolder/>
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

<#assign numExtendedFields=num_extended_fields(fieldList)>
<#function extended_field_index numFields numExtendedFields fieldIndex>
    <#return fieldIndex - (numFields - numExtendedFields)>
</#function>
<#macro field_default_constructor_arguments field>
    <#if field.initializer??>
        <#-- cannot be compound or array since it has initializer! -->
        <#if field.optional??>
            <#if field.holderNeedsAllocator>allocator<#else>::zserio::InPlace</#if>, <#t>
        </#if>
        <#if field.typeInfo.isString>
        ::zserio::stringViewToString(${field.initializer}, allocator)<#t>
        <#else>
        static_cast<${field.typeInfo.typeFullName}>(${field.initializer})<#t>
        <#if field.needsAllocator>, allocator</#if><#t>
        </#if>
    <#else>
        <#if field.optional??>
            ::zserio::NullOpt<#if field.holderNeedsAllocator>, allocator</#if><#t>
        <#else>
            <#if field.array??>
                allocator<#t>
            <#else>
                <#if field.needsAllocator>allocator<#elseif field.typeInfo.isSimple>${field.typeInfo.typeFullName}()</#if><#t>
            </#if>
        </#if>
    </#if>
</#macro>
<#macro empty_constructor_field_initialization>
    <#list fieldList as field>
        <@field_member_name field/>(<@field_default_constructor_arguments field/>)
    </#list>
</#macro>
<#if withSettersCode>
    <#assign emptyConstructorInitMacroName><#if fieldList?has_content>empty_constructor_field_initialization</#if></#assign>
    <@compound_constructor_definition compoundConstructorsData, emptyConstructorInitMacroName/>

</#if>
<#macro read_constructor_field_initialization packed>
    <#list fieldList as field>
        <@field_member_name field/>(${field.readerName}(<#if packed && field.isPackable>context, </#if>in<#rt>
        <#if field.needsAllocator || field.holderNeedsAllocator>
                , allocator<#t>
        </#if>
                <#lt>))
    </#list>
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

    static const ::zserio::StructTypeInfo<allocator_type> typeInfo = {
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
        fields, parameters, functions
    };

    return typeInfo;
}

    <#if withReflectionCode>
<#macro structure_reflectable isConst>
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
                    "Writer code is disabled by '-withoutWriterCode' zserio option!";
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

        const ::zserio::ParsingInfo& parsingInfo() const override
        {
    <#if withParsingInfoCode>
            return m_object.parsingInfo();
    <#else>
            throw ::zserio::CppRuntimeException("Reflectable '${name}': ") <<
                    "Parsing information code is disabled by '-withoutParsingInfoCode' zserio option!";
    </#if>
        }

    private:
        <#if isConst>const </#if>${fullName}& m_object;
    };

    return std::allocate_shared<Reflectable>(allocator, *this, allocator);
}
</#macro>
<@structure_reflectable true/>

        <#if withWriterCode>
<@structure_reflectable false/>

        </#if>
    </#if>
</#if>
<#if needs_compound_initialization(compoundConstructorsData)>
<@compound_initialize_definition compoundConstructorsData, needsChildrenInitialization/>

</#if>
<#if needsChildrenInitialization>
void ${name}::initializeChildren()
{
    <#list fieldList as field>
    <@compound_initialize_children_field field, 1/>
    </#list>
    <@compound_initialize_children_epilog_definition compoundConstructorsData/>
}

</#if>
<@compound_parameter_accessors_definition name, compoundParametersData/>
<#list fieldList as field>
    <#if needs_field_getter(field)>
<@field_raw_cpp_type_name field/>& ${name}::${field.getterName}()
{
    return <@compound_get_field field/><#if field.array??>.getRawArray()</#if>;
}

    </#if>
<@field_raw_cpp_argument_type_name field/> ${name}::${field.getterName}() const
{
    return <@compound_get_field field/><#if field.array??>.getRawArray()</#if>;
}

    <#if needs_field_setter(field)>
void ${name}::${field.setterName}(<@field_raw_cpp_argument_type_name field/> <@field_argument_name field/>)
{
        <#if field.isExtended>
    if (!${field.isPresentIndicatorName}())
    {
        m_numExtendedFields = ${numExtendedFields};
    }
        </#if>
    <@field_member_name field/> = <@compound_setter_field_value field/>;
}

    </#if>
    <#if needs_field_rvalue_setter(field)>
void ${name}::${field.setterName}(<@field_raw_cpp_type_name field/>&& <@field_argument_name field/>)
{
        <#if field.isExtended>
    if (!${field.isPresentIndicatorName}())
    {
        m_numExtendedFields = ${numExtendedFields};
    }
        </#if>
    <@field_member_name field/> = <@compound_setter_field_rvalue field/>;
}

    </#if>
    <#if field.isExtended>
bool ${name}::${field.isPresentIndicatorName}() const
{
    return m_numExtendedFields > ${extended_field_index(fieldList?size, numExtendedFields, field?index)};
}

    </#if>
    <#if field.optional??>
bool ${name}::${field.optional.isUsedIndicatorName}() const
{
    return (<@field_optional_condition field/>);
}

        <#if withSettersCode>
bool ${name}::${field.optional.isSetIndicatorName}() const
{
    return <@field_member_name field/>.hasValue();
}

void ${name}::${field.optional.resetterName}()
{
            <#if field.isExtended>
    if (!${field.isPresentIndicatorName}())
    {
        m_numExtendedFields = ${numExtendedFields};
    }
            </#if>
    <@field_member_name field/>.reset();
}

        </#if>
    </#if>
</#list>
<@compound_functions_definition name, compoundFunctionsData/>
<#if isPackable && usedInPackedArray>
void ${name}::initPackingContext(${name}::ZserioPackingContext&<#if uses_packing_context(fieldList)> context</#if>) const
{
    <#list fieldList as field>
    <@compound_init_packing_context_field field, 1/>
    </#list>
}

</#if>
size_t ${name}::bitSizeOf(size_t<#if fieldList?has_content> bitPosition</#if>) const
{
<#if fieldList?has_content>
    size_t endBitPosition = bitPosition;

    <#list fieldList as field>
    <@compound_bitsizeof_field field, 1/>
    </#list>

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

    <#list fieldList as field>
    <@compound_bitsizeof_field field, 1, true/>
    </#list>

    return endBitPosition - bitPosition;
}
</#if>
<#if withWriterCode>

size_t ${name}::initializeOffsets(size_t bitPosition)
{
    <#if fieldList?has_content>
    size_t endBitPosition = bitPosition;

        <#list fieldList as field>
    <@compound_initialize_offsets_field field, 1/>
        </#list>

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

        <#list fieldList as field>
    <@compound_initialize_offsets_field field, 1, true/>
        </#list>

    return endBitPosition;
}
    </#if>
</#if>

<#macro structure_compare_field field indent>
    <#local I>${""?left_pad(indent * 4)}</#local>
    <#if field.optional??>
        <#-- if optional is not auto and is used the other should be is used as well because all previous paramaters
             and fields were the same. -->
${I}(!${field.optional.isUsedIndicatorName}() ? !other.${field.optional.isUsedIndicatorName}() : <#rt>
            (<@field_member_name field/> == other.<@field_member_name field/>))<#t>
    <#else>
${I}(<@field_member_name field/> == other.<@field_member_name field/>)<#rt>
    </#if>
</#macro>
bool ${name}::operator==(const ${name}&<#if compoundParametersData.list?has_content || fieldList?has_content> other</#if>) const
{
<#if compoundParametersData.list?has_content || fieldList?has_content>
    if (this != &other)
    {
        return
                <@compound_parameter_comparison compoundParametersData, fieldList?has_content/>
    <#list fieldList as field>
        <#if field.isExtended>
                (!${field.isPresentIndicatorName}() ?
                        !other.${field.isPresentIndicatorName}() :
                        (other.${field.isPresentIndicatorName}() && <@structure_compare_field field, 0/>))<#if field?has_next> &&<#else>;</#if>
        <#else>
                <#lt><@structure_compare_field field, 4/><#if field?has_next> &&<#else>;</#if>
        </#if>
    </#list>
    }

</#if>
    return true;
}

<#macro structure_less_than_field field indent>
    <#local I>${""?left_pad(indent * 4)}</#local>
    <#local lhs><@field_member_name field/></#local>
    <#local rhs>other.<@field_member_name field/></#local>
    <#if field.optional??>
${I}if (${field.optional.isUsedIndicatorName}() && other.${field.optional.isUsedIndicatorName}())
${I}{
${I}    if (<@compound_field_less_than_compare field, lhs, rhs/>)
${I}    {
${I}        return true;
${I}    }
${I}    if (<@compound_field_less_than_compare field, rhs, lhs/>)
${I}    {
${I}        return false;
${I}    }
${I}}
${I}else if (${field.optional.isUsedIndicatorName}() != other.${field.optional.isUsedIndicatorName}())
${I}{
${I}    return !${field.optional.isUsedIndicatorName}();
${I}}
    <#else>
${I}if (<@compound_field_less_than_compare field, lhs, rhs/>)
${I}{
${I}    return true;
${I}}
${I}if (<@compound_field_less_than_compare field, rhs, lhs/>)
${I}{
${I}    return false;
${I}}
    </#if>
</#macro>
bool ${name}::operator<(const ${name}&<#if compoundParametersData.list?has_content || fieldList?has_content> other</#if>) const
{
    <@compound_parameter_less_than compoundParametersData, 1/>
<#if fieldList?has_content>
    <#list fieldList as field>
        <#if field.isExtended>
    if (${field.isPresentIndicatorName}() && other.${field.isPresentIndicatorName}())
    {
        <@structure_less_than_field field, 2/>
    }
    else if (${field.isPresentIndicatorName}() != other.${field.isPresentIndicatorName}())
    {
        return !${field.isPresentIndicatorName}();
    }
        <#else>
    <@structure_less_than_field field, 1/>
        </#if>

    </#list>
</#if>
    return false;
}

<#macro structure_hash_code_field field indent>
    <#local I>${""?left_pad(indent * 4)}</#local>
    <#if field.optional??>
${I}if (${field.optional.isUsedIndicatorName}())
${I}{
${I}    result = ::zserio::calcHashCode(result, <@field_member_name field/>);
${I}}
    <#else>
${I}result = ::zserio::calcHashCode(result, <@field_member_name field/>);
    </#if>
</#macro>
uint32_t ${name}::hashCode() const
{
    uint32_t result = ::zserio::HASH_SEED;

    <@compound_parameter_hash_code compoundParametersData/>
<#list fieldList as field>
    <#if field.isExtended>
    if (${field.isPresentIndicatorName}())
    {
        <@structure_hash_code_field field, 2/>
    }
    <#else>
    <@structure_hash_code_field field, 1/>
    </#if>
    <#if !field?has_next>

    </#if>
</#list>
    return result;
}
<#if withWriterCode>

<#assign needsWriteNewLines=false/>
<#list fieldList as field>
    <#if needs_field_any_write_check_code(field, name, 2)>
        <#assign needsWriteNewLines=true/>
        <#break>
    </#if>
</#list>
void ${name}::write(::zserio::BitStreamWriter&<#if fieldList?has_content> out</#if>) const
{
    <#if fieldList?has_content>
        <#list fieldList as field>
    <@compound_write_field field, name, 1/>
            <#if field?has_next && needsWriteNewLines>

            </#if>
        </#list>
    </#if>
}
    <#if isPackable && usedInPackedArray>

void ${name}::write(${name}::ZserioPackingContext&<#if uses_packing_context(fieldList)> context</#if>, <#rt>
        <#lt>::zserio::BitStreamWriter& out) const
{
        <#list fieldList as field>
    <@compound_write_field field, name, 1, true/>
            <#if field?has_next && needsWriteNewLines>

            </#if>
        </#list>
}
    </#if>
</#if>
<#if withParsingInfoCode>

const ::zserio::ParsingInfo& ${name}::parsingInfo() const
{
    return m_parsingInfo;
}
</#if>
<#if fieldList?has_content>

<@inner_classes_definition name, fieldList/>
</#if>
<#list fieldList as field>
<@field_member_type_name field, name/> ${name}::${field.readerName}(::zserio::BitStreamReader& in<#rt>
    <#if field.needsAllocator || field.holderNeedsAllocator>
        <#lt>,
        const allocator_type& allocator<#rt>
    </#if>
    <#lt>)
{
    <#if field.isExtended>
    if (::zserio::alignTo(UINT8_C(8), in.getBitPosition()) >= in.getBufferBitSize())
    {
        <#if !field.typeInfo.isSimple && !(field.typeInfo.isString && field.initializer??)>
        return <@field_member_type_name field/>(<@field_default_constructor_arguments field/>);
        <#else>
        return <@field_default_constructor_arguments field/>;
        </#if>
    }
    ++m_numExtendedFields;
    in.alignTo(UINT32_C(8));

    </#if>
    <@compound_read_field field, name, 1/>
}
    <#if field.isPackable && usedInPackedArray>

<@field_member_type_name field, name/> ${name}::${field.readerName}(<#rt>
        <#lt>${name}::ZserioPackingContext&<#if uses_field_packing_context(field)> context</#if>, <#rt>
        ::zserio::BitStreamReader& in<#t>
        <#if field.needsAllocator || field.holderNeedsAllocator>
        , const allocator_type& allocator<#t>
        </#if>
        <#lt>)
{
        <#if field.isExtended>
    if (::zserio::alignTo(UINT8_C(8), in.getBitPosition()) >= in.getBufferBitSize())
    {
            <#if !field.typeInfo.isSimple && !(field.typeInfo.isString && field.initializer??)>
        return <@field_member_type_name field/>(<@field_default_constructor_arguments field/>);
            <#else>
        return <@field_default_constructor_arguments field/>;
            </#if>
    }
    ++m_numExtendedFields;
    in.alignTo(UINT32_C(8));

        </#if>
    <@compound_read_field field, name, 1, true/>
}
    </#if>

</#list>
<@namespace_end package.path/>
