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
<@system_includes cppSystemIncludes/>

<@user_include package.path, "${name}.h"/>
<@user_includes cppUserIncludes, false/>
<@namespace_begin package.path/>

<@inner_classes_definition name, fieldList/>
<#macro empty_constructor_field_initialization>
    <#list fieldList as field>
        <@field_member_name field/>(<#rt>
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
                    <@array_traits field/>, allocator<#t>
                <#else>
                    <#if field.needsAllocator>allocator<#elseif field.typeInfo.isSimple>${field.typeInfo.typeFullName}()</#if><#t>
                </#if>
            </#if>
        </#if>
        <#lt>)<#if field?has_next>,</#if>
    </#list>
</#macro>
<#if withWriterCode>
    <#assign emptyConstructorInitMacroName><#if fieldList?has_content>empty_constructor_field_initialization</#if></#assign>
    <@compound_constructor_definition compoundConstructorsData, emptyConstructorInitMacroName/>

</#if>
<#macro read_constructor_field_initialization packed>
    <#list fieldList as field>
        <@field_member_name field/>(${field.readerName}(<#if packed && field.isPackable>contextNode, </#if>in<#rt>
        <#if field.needsAllocator || field.holderNeedsAllocator>
                , allocator<#t>
        </#if>
                <#lt>))<#if field?has_next>,</#if>
    </#list>
</#macro>
<#assign readConstructorInitMacroName><#if fieldList?has_content>read_constructor_field_initialization</#if></#assign>
<@compound_read_constructor_definition compoundConstructorsData, readConstructorInitMacroName/>

<@compound_read_constructor_definition compoundConstructorsData, readConstructorInitMacroName, true/>

<#if needs_compound_initialization(compoundConstructorsData) || has_field_with_initialization(fieldList)>
<@compound_copy_constructor_definition compoundConstructorsData/>

<@compound_assignment_operator_definition compoundConstructorsData/>

<@compound_move_constructor_definition compoundConstructorsData/>

<@compound_move_assignment_operator_definition compoundConstructorsData/>

</#if>
<@compound_allocator_propagating_copy_constructor_definition compoundConstructorsData/>

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
    <#if withReflectionCode>
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
        using ::zserio::Reflectable<#if isConst>Const</#if>AllocatorHolderBase<allocator_type>::getField;
        using ::zserio::Reflectable<#if isConst>Const</#if>AllocatorHolderBase<allocator_type>::getParameter;
        using ::zserio::Reflectable<#if isConst>Const</#if>AllocatorHolderBase<allocator_type>::callFunction;

    </#if>
        explicit Reflectable(<#if isConst>const </#if>${fullName}& object, const allocator_type& allocator) :
                ::zserio::Reflectable<#if isConst>Const</#if>AllocatorHolderBase<allocator_type>(<#rt>
                        <#lt>${fullName}::typeInfo(), allocator),
                m_object(object)
        {}
    <#if fieldList?has_content>
        <#if !isConst>

        virtual void initializeChildren()
        {
            <#if needsChildrenInitialization>
            m_object.initializeChildren();
            </#if>
        }
        </#if>

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

        virtual void write(::zserio::BitStreamWriter& writer) const override
        {
            m_object.write(writer);
        }

        virtual size_t bitSizeOf(size_t bitPosition) const override
        {
            return m_object.bitSizeOf(bitPosition);
        }

    private:
        <#if isConst>const </#if>${fullName}& m_object;
    };

    return std::allocate_shared<Reflectable>(allocator, *this, allocator);
}
</#macro>
<@structure_reflectable true/>

<@structure_reflectable false/>

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
    <@field_member_name field/> = <@compound_setter_field_value field/>;
}

    </#if>
    <#if needs_field_rvalue_setter(field)>
void ${name}::${field.setterName}(<@field_raw_cpp_type_name field/>&& <@field_argument_name field/>)
{
    <@field_member_name field/> = <@compound_setter_field_rvalue field/>;
}

    </#if>
    <#if field.optional??>
bool ${name}::${field.optional.isUsedIndicatorName}() const
{
    return (<@field_optional_condition field/>);
}

        <#if withWriterCode>
bool ${name}::${field.optional.isSetIndicatorName}() const
{
    return <@field_member_name field/>.hasValue();
}

void ${name}::${field.optional.resetterName}()
{
    <@field_member_name field/>.reset();
}

        </#if>
    </#if>
</#list>
<@compound_functions_definition name, compoundFunctionsData/>
void ${name}::createPackingContext(${types.packingContextNode.name}&<#if fieldList?has_content> contextNode</#if>)
{
<#list fieldList as field>
    <@compound_create_packing_context_field field/>
</#list>
}

void ${name}::initPackingContext(${types.packingContextNode.name}&<#rt>
        <#lt><#if needs_packing_context_node(fieldList)> contextNode</#if>) const
{
<#list fieldList as field>
    <@compound_init_packing_context_field field, field?index, 1/>
</#list>
}

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

size_t ${name}::bitSizeOf(${types.packingContextNode.name}&<#rt>
        <#if needs_packing_context_node(fieldList)> contextNode</#if>, <#t>
        <#lt>size_t<#if fieldList?has_content> bitPosition</#if>) const
{
<#if fieldList?has_content>
    size_t endBitPosition = bitPosition;

    <#list fieldList as field>
    <@compound_bitsizeof_field field, 1, true, field?index/>
    </#list>

    return endBitPosition - bitPosition;
<#else>
    return 0;
</#if>
}
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

size_t ${name}::initializeOffsets(${types.packingContextNode.name}&<#rt>
        <#lt><#if needs_packing_context_node(fieldList)> contextNode</#if>, size_t bitPosition)
{
    <#if fieldList?has_content>
    size_t endBitPosition = bitPosition;

        <#list fieldList as field>
    <@compound_initialize_offsets_field field, 1, true, field?index/>
        </#list>

    return endBitPosition;
    <#else>
    return bitPosition;
    </#if>
}
</#if>

bool ${name}::operator==(const ${name}&<#if compoundParametersData.list?has_content || fieldList?has_content> other</#if>) const
{
<#if compoundParametersData.list?has_content || fieldList?has_content>
    if (this != &other)
    {
        return
                <@compound_parameter_comparison compoundParametersData, fieldList?has_content/>
    <#list fieldList as field>
        <#if field.optional??>
                <#-- if optional is not auto and is used the other should be is used as well because all previous paramaters
                and fields were the same. -->
                ((!${field.optional.isUsedIndicatorName}()) ? !other.${field.optional.isUsedIndicatorName}() : <#rt>
                <#lt>(<@field_member_name field/> == other.<@field_member_name field/>))<#if field?has_next> &&<#else>;</#if>
        <#else>
                (<@field_member_name field/> == other.<@field_member_name field/>)<#if field?has_next> &&<#else>;</#if>
        </#if>
    </#list>
    }

</#if>
    return true;
}

uint32_t ${name}::hashCode() const
{
    uint32_t result = ::zserio::HASH_SEED;

    <@compound_parameter_hash_code compoundParametersData/>
<#list fieldList as field>
    <#if field.optional??>
    if (${field.optional.isUsedIndicatorName}())
        result = ::zserio::calcHashCode(result, <@field_member_name field/>);
    <#else>
    result = ::zserio::calcHashCode(result, <@field_member_name field/>);
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

void ${name}::write(${types.packingContextNode.name}&<#rt>
        <#if needs_packing_context_node(fieldList)> contextNode</#if>, <#t>
        <#lt>::zserio::BitStreamWriter&<#if fieldList?has_content> out</#if>) const
{
    <#if fieldList?has_content>
        <#list fieldList as field>
    <@compound_write_field field, name, 1, true, field?index/>
            <#if field?has_next && needsWriteNewLines>

            </#if>
        </#list>
    </#if>
}
</#if>
<#list fieldList as field>

<@field_member_type_name field, name/> ${name}::${field.readerName}(::zserio::BitStreamReader& in<#rt>
    <#if field.needsAllocator || field.holderNeedsAllocator>
        <#lt>,
        const allocator_type& allocator<#rt>
    </#if>
    <#lt>)
{
    <@compound_read_field field, name, 1/>
}
    <#if field.isPackable>

<@field_member_type_name field, name/> ${name}::${field.readerName}(${types.packingContextNode.name}&<#rt>
        <#if field_needs_packing_context_node(field)> contextNode</#if>, ::zserio::BitStreamReader& in<#t>
        <#if field.needsAllocator || field.holderNeedsAllocator>
        , const allocator_type& allocator<#t>
        </#if>
        <#lt>)
{
    <@compound_read_field field, name, 1, true, field?index/>
}
    </#if>
</#list>
<@namespace_end package.path/>
