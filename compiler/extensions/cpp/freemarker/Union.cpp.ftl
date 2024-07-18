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
<#assign choiceTagArrayTraits="::zserio::VarSizeArrayTraits">
<@namespace_begin package.path/>

<#macro empty_constructor_field_initialization>
        m_choiceTag(UNDEFINED_CHOICE)<#rt>
        <#if fieldList?has_content>
        <#lt>,
        m_objectChoice(allocator)
        </#if>
</#macro>
<#if withWriterCode>
    <@compound_constructor_definition compoundConstructorsData, "empty_constructor_field_initialization"/>

</#if>
<#macro read_constructor_field_initialization packed>
    <#if fieldList?has_content>
        m_choiceTag(readChoiceTag(<#if packed>context, </#if>in)),
        m_objectChoice(readObject(<#if packed>context, </#if>in, allocator))
    <#else>
        m_choiceTag(UNDEFINED_CHOICE)
    </#if>
</#macro>
<@compound_read_constructor_definition compoundConstructorsData, "read_constructor_field_initialization"/>
<#if isPackable && usedInPackedArray>

<@compound_read_constructor_definition compoundConstructorsData, "read_constructor_field_initialization", true/>
</#if>

<#if needs_compound_initialization(compoundConstructorsData) || has_field_with_initialization(fieldList)>
${name}::${name}(const ${name}& other) :
        m_choiceTag(other.m_choiceTag)<#rt>
    <#if fieldList?has_content>
        <#lt>,
        <#list fieldList as field>
        <@compound_copy_constructor_initializer_field field, field?has_next, 2/>
            <#if field.usesAnyHolder>
                <#break>
            </#if>
        </#list>
    <#else>

    </#if>
{
    <@compound_copy_initialization compoundConstructorsData/>
}

${name}& ${name}::operator=(const ${name}& other)
{
    m_choiceTag = other.m_choiceTag;
    <#list fieldList as field>
    <@compound_assignment_field field, 1/>
        <#if field.usesAnyHolder>
            <#break>
        </#if>
    </#list>
    <@compound_copy_initialization compoundConstructorsData/>

    return *this;
}

${name}::${name}(${name}&& other) :
        m_choiceTag(other.m_choiceTag)<#rt>
    <#if fieldList?has_content>
        <#lt>,
        <#list fieldList as field>
        <@compound_move_constructor_initializer_field field, field?has_next, 2/>
            <#if field.usesAnyHolder>
                <#break>
            </#if>
        </#list>
    <#else>

    </#if>
{
    <@compound_copy_initialization compoundConstructorsData/>
}

${name}& ${name}::operator=(${name}&& other)
{
    m_choiceTag = other.m_choiceTag;
    <#list fieldList as field>
    <@compound_move_assignment_field field, 1/>
        <#if field.usesAnyHolder>
            <#break>
        </#if>
    </#list>
    <@compound_copy_initialization compoundConstructorsData/>

    return *this;
}

</#if>
<#if needs_compound_initialization(compoundConstructorsData)>
${name}::${name}(::zserio::NoInitT, const ${name}& other) :
    <#if needs_compound_initialization(compoundConstructorsData)>
        m_isInitialized(false),
    <#elseif has_field_with_initialization(compoundConstructorsData.fieldList)>
        m_areChildrenInitialized(false),
    </#if>
        m_choiceTag(other.m_choiceTag)<#rt>
    <#if fieldList?has_content>
        <#lt>,
        <#list fieldList as field>
        <@compound_copy_constructor_initializer_field field, field?has_next, 2/>
            <#if field.usesAnyHolder>
                <#break>
            </#if>
        </#list>
    <#else>

    </#if>
{
}

${name}& ${name}::assign(::zserio::NoInitT, const ${name}& other)
{
    <#if needs_compound_initialization(compoundConstructorsData)>
    m_isInitialized = false;
    <#elseif has_field_with_initialization(compoundConstructorsData.fieldList)>
    m_areChildrenInitialized = false;
    </#if>
    m_choiceTag = other.m_choiceTag;
    <#list fieldList as field>
    <@compound_assignment_field field, 1/>
        <#if field.usesAnyHolder>
            <#break>
        </#if>
    </#list>

    return *this;
}

${name}::${name}(::zserio::NoInitT, ${name}&& other) :
    <#if needs_compound_initialization(compoundConstructorsData)>
        m_isInitialized(false),
    <#elseif has_field_with_initialization(compoundConstructorsData.fieldList)>
        m_areChildrenInitialized(false),
    </#if>
        m_choiceTag(other.m_choiceTag)<#rt>
    <#if fieldList?has_content>
        <#lt>,
        <#list fieldList as field>
        <@compound_move_constructor_initializer_field field, field?has_next, 2/>
            <#if field.usesAnyHolder>
                <#break>
            </#if>
        </#list>
    <#else>

    </#if>
{
}

${name}& ${name}::assign(::zserio::NoInitT, ${name}&& other)
{
    m_choiceTag = other.m_choiceTag;
    <#if needs_compound_initialization(compoundConstructorsData)>
    m_isInitialized = false;
    <#elseif has_field_with_initialization(compoundConstructorsData.fieldList)>
    m_areChildrenInitialized = false;
    </#if>
    <#list fieldList as field>
    <@compound_move_assignment_field field, 1/>
        <#if field.usesAnyHolder>
            <#break>
        </#if>
    </#list>

    return *this;
}

</#if>
${name}::${name}(::zserio::PropagateAllocatorT,
        const ${name}& other, const allocator_type&<#rt>
        <#lt><#if fieldList?has_content> allocator</#if>) :
        m_choiceTag(other.m_choiceTag)<#rt>
    <#if fieldList?has_content>
        <#lt>,
        <#list fieldList as field>
        <@compound_allocator_propagating_copy_constructor_initializer_field field, field?has_next, 2/>
            <#if field.usesAnyHolder>
                <#break>
            </#if>
        </#list>
    <#else>

    </#if>
{
    <@compound_copy_initialization compoundConstructorsData/>
}

<#if needs_compound_initialization(compoundConstructorsData)>

${name}::${name}(::zserio::PropagateAllocatorT, ::zserio::NoInitT,
        const ${name}& other, const allocator_type&<#rt>
        <#lt><#if fieldList?has_content> allocator</#if>) :
        m_choiceTag(other.m_choiceTag)<#rt>
    <#if fieldList?has_content>
        <#lt>,
        <#list fieldList as field>
        <@compound_allocator_propagating_copy_constructor_initializer_field field, field?has_next, 2/>
            <#if field.usesAnyHolder>
                <#break>
            </#if>
        </#list>
    <#else>

    </#if>
{
}
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

    static const ::zserio::UnionTypeInfo<allocator_type> typeInfo = {
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
<#macro union_reflectable isConst>
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
                ::zserio::Reflectable<#if isConst>Const</#if>AllocatorHolderBase<allocator_type>(${fullName}::typeInfo(), alloc),
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
            switch (m_object.choiceTag())
            {
        <#list fieldList as field>
            case <@choice_tag_name field/>:
                return ::zserio::makeStringView("${field.name}");
        </#list>
            default:
                return {};
            }
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

    <#if withBitPosition>
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
<@union_reflectable true/>

        <#if withWriterCode>
<@union_reflectable false/>

        </#if>
    </#if>
</#if>
<#if needs_compound_initialization(compoundConstructorsData)>
<@compound_initialize_definition compoundConstructorsData, needsChildrenInitialization/>

</#if>
<#if needsChildrenInitialization>
void ${name}::initializeChildren()
{
    <#if fieldList?has_content>
    switch (m_choiceTag)
    {
        <#list fieldList as field>
    case <@choice_tag_name field/>:
        <@compound_initialize_children_field field, 2, true/>
        break;
        </#list>
    default:
        throw ::zserio::CppRuntimeException("No match in union ${name}!");
    }
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
    m_choiceTag = <@choice_tag_name field/>;
    m_objectChoice = <@compound_setter_field_value field/>;
}

    </#if>
    <#if needs_field_rvalue_setter(field)>
void ${name}::${field.setterName}(<@field_raw_cpp_type_name field/>&& <@field_argument_name field/>)
{
    m_choiceTag = <@choice_tag_name field/>;
    m_objectChoice = <@compound_setter_field_rvalue field/>;
}

    </#if>
</#list>
<@compound_functions_definition name, compoundFunctionsData/>
${name}::ChoiceTag ${name}::choiceTag() const
{
    return m_choiceTag;
}
<#if isPackable && usedInPackedArray>

void ${name}::initPackingContext(${name}::ZserioPackingContext& context) const
{
    context.getChoiceTag().init<${choiceTagArrayTraits}>(static_cast<uint32_t>(m_choiceTag));

    switch (m_choiceTag)
    {
    <#list fieldList as field>
    case <@choice_tag_name field/>:
        <@compound_init_packing_context_field field, 2/>
        break;
    </#list>
    default:
        throw ::zserio::CppRuntimeException("No match in union ${name}!");
    }
}
</#if>

size_t ${name}::bitSizeOf(size_t<#if fieldList?has_content> bitPosition</#if>) const
{
<#if fieldList?has_content>
    size_t endBitPosition = bitPosition;

    endBitPosition += ::zserio::bitSizeOfVarSize(static_cast<uint32_t>(m_choiceTag));

    switch (m_choiceTag)
    {
    <#list fieldList as field>
    case <@choice_tag_name field/>:
        <@compound_bitsizeof_field field, 2/>
        break;
    </#list>
    default:
        throw ::zserio::CppRuntimeException("No match in union ${name}!");
    }

    return endBitPosition - bitPosition;
<#else>
    return 0;
</#if>
}
<#if isPackable && usedInPackedArray>

size_t ${name}::bitSizeOf(${name}::ZserioPackingContext& context, size_t bitPosition) const
{
    size_t endBitPosition = bitPosition;

    endBitPosition += context.getChoiceTag().bitSizeOf<${choiceTagArrayTraits}>(static_cast<uint32_t>(m_choiceTag));

    switch (m_choiceTag)
    {
    <#list fieldList as field>
    case <@choice_tag_name field/>:
        <@compound_bitsizeof_field field, 2, true/>
        break;
    </#list>
    default:
        throw ::zserio::CppRuntimeException("No match in union ${name}!");
    }

    return endBitPosition - bitPosition;
}
</#if>
<#if withWriterCode>

size_t ${name}::initializeOffsets(size_t bitPosition)
{
    <#if fieldList?has_content>
    size_t endBitPosition = bitPosition;

    endBitPosition += ::zserio::bitSizeOfVarSize(static_cast<uint32_t>(m_choiceTag));

    switch (m_choiceTag)
    {
        <#list fieldList as field>
    case <@choice_tag_name field/>:
        <@compound_initialize_offsets_field field, 2/>
        break;
        </#list>
    default:
        throw ::zserio::CppRuntimeException("No match in union ${name}!");
    }

    return endBitPosition;
    <#else>
    return bitPosition;
    </#if>
}
    <#if isPackable && usedInPackedArray>

size_t ${name}::initializeOffsets(${name}::ZserioPackingContext& context, size_t bitPosition)
{
    size_t endBitPosition = bitPosition;

    endBitPosition += context.getChoiceTag().bitSizeOf<${choiceTagArrayTraits}>(static_cast<uint32_t>(m_choiceTag));

    switch (m_choiceTag)
    {
        <#list fieldList as field>
    case <@choice_tag_name field/>:
        <@compound_initialize_offsets_field field, 2, true/>
        break;
        </#list>
    default:
        throw ::zserio::CppRuntimeException("No match in union ${name}!");
    }

    return endBitPosition;
}
    </#if>
</#if>

bool ${name}::operator==(const ${name}& other) const
{
    if (this == &other)
    {
        return true;
    }

    <@compound_parameter_comparison_with_any_holder compoundParametersData/>
    if (m_choiceTag != other.m_choiceTag)
    {
        return false;
    }

<#if fieldList?has_content>
    if (m_objectChoice.hasValue() != other.m_objectChoice.hasValue())
    {
        return false;
    }

    if (!m_objectChoice.hasValue())
    {
        return true;
    }

    switch (m_choiceTag)
    {
    <#list fieldList as field>
    case <@choice_tag_name field/>:
        return m_objectChoice.get<<@field_cpp_type_name field/>>() == other.m_objectChoice.get<<@field_cpp_type_name field/>>();
    </#list>
    default:
        return true; // UNDEFINED_CHOICE
    }
<#else>
    return true;
</#if>
}

<#macro union_less_than_field field indent>
    <#local I>${""?left_pad(indent * 4)}</#local>
    <#local lhs>m_objectChoice.get<<@field_cpp_type_name field/>>()</#local>
    <#local rhs>other.m_objectChoice.get<<@field_cpp_type_name field/>>()</#local>
${I}if (m_objectChoice.hasValue() && other.m_objectChoice.hasValue())
${I}{
${I}    return <@compound_field_less_than_compare field, lhs, rhs/>;
${I}}
${I}else
${I}{
${I}    return !m_objectChoice.hasValue() && other.m_objectChoice.hasValue();
${I}}
</#macro>
bool ${name}::operator<(const ${name}& other) const
{
    <@compound_parameter_less_than compoundParametersData, 1/>
    if (m_choiceTag < other.m_choiceTag)
    {
        return true;
    }
    if (other.m_choiceTag < m_choiceTag)
    {
        return false;
    }
<#if fieldList?has_content>

    switch (m_choiceTag)
    {
    <#list fieldList as field>
    case <@choice_tag_name field/>:
        <@union_less_than_field field, 2/>
    </#list>
    default:
        return false; // UNDEFINED_CHOICE
    }
<#else>
    return false;
</#if>
}

uint32_t ${name}::hashCode() const
{
    uint32_t result = ::zserio::HASH_SEED;

    <@compound_parameter_hash_code compoundParametersData/>
    result = ::zserio::calcHashCode(result, static_cast<int32_t>(m_choiceTag));
<#if fieldList?has_content>
    if (m_objectChoice.hasValue())
    {
        switch (m_choiceTag)
        {
        <#list fieldList as field>
        case <@choice_tag_name field/>:
            result = ::zserio::calcHashCode(result, m_objectChoice.get<<@field_cpp_type_name field/>>());
            break;
        </#list>
        default:
            // UNDEFINED_CHOICE
            break;
        }
    }
</#if>

    return result;
}
<#if withWriterCode>

void ${name}::write(::zserio::BitStreamWriter&<#if fieldList?has_content> out</#if>) const
{
    <#if fieldList?has_content>
    out.writeVarSize(static_cast<uint32_t>(m_choiceTag));

    switch (m_choiceTag)
    {
        <#list fieldList as field>
    case <@choice_tag_name field/>:
        <@compound_write_field field, name, 2/>
        break;
        </#list>
    default:
        throw ::zserio::CppRuntimeException("No match in union ${name}!");
    }
    </#if>
}
    <#if isPackable && usedInPackedArray>

void ${name}::write(${name}::ZserioPackingContext& context, ::zserio::BitStreamWriter& out) const
{
    context.getChoiceTag().write<${choiceTagArrayTraits}>(out, static_cast<uint32_t>(m_choiceTag));

    switch (m_choiceTag)
    {
        <#list fieldList as field>
    case <@choice_tag_name field/>:
        <@compound_write_field field, name, 2, true/>
        break;
        </#list>
    default:
        throw ::zserio::CppRuntimeException("No match in union ${name}!");
    }
}
    </#if>
</#if>
<#if withBitPosition>
size_t ${name}::bitPosition() const
{
    return m_bitPosition;
}
</#if>
<#if fieldList?has_content>

<@inner_classes_definition name, fieldList/>
${name}::ChoiceTag ${name}::readChoiceTag(::zserio::BitStreamReader& in)
{
    return static_cast<${name}::ChoiceTag>(static_cast<int32_t>(in.readVarSize()));
}
<#if isPackable && usedInPackedArray>

${name}::ChoiceTag ${name}::readChoiceTag(${name}::ZserioPackingContext& context, ::zserio::BitStreamReader& in)
{
    return static_cast<${name}::ChoiceTag>(static_cast<int32_t>(context.getChoiceTag().read<${choiceTagArrayTraits}>(in)));
}
</#if>

${types.anyHolder.name} ${name}::readObject(::zserio::BitStreamReader& in, const allocator_type& allocator)
{
    switch (m_choiceTag)
    {
        <#list fieldList as field>
    case <@choice_tag_name field/>:
            <#if needs_field_read_local_variable(field)>
        {
            <@compound_read_field field, name, 3/>
        }
            <#else>
        <@compound_read_field field, name, 2/>
            </#if>
        </#list>
    default:
        throw ::zserio::CppRuntimeException("No match in union ${name}!");
    }
}
<#if isPackable && usedInPackedArray>

${types.anyHolder.name} ${name}::readObject(${name}::ZserioPackingContext&<#if uses_packing_context(fieldList)> context</#if>,
        ::zserio::BitStreamReader& in, const allocator_type& allocator)
{
    switch (m_choiceTag)
    {
    <#list fieldList as field>
    case <@choice_tag_name field/>:
        <#if needs_field_read_local_variable(field)>
        {
            <@compound_read_field field, name, 3, true/>
        }
        <#else>
        <@compound_read_field field, name, 2, true/>
        </#if>
    </#list>
    default:
        throw ::zserio::CppRuntimeException("No match in union ${name}!");
    }
}
</#if>

${types.anyHolder.name} ${name}::copyObject(const allocator_type& allocator) const
{
    switch (m_choiceTag)
    {
        <#list fieldList as field>
    case <@choice_tag_name field/>:
        return ::zserio::allocatorPropagatingCopy<<@field_cpp_type_name field/>>(m_objectChoice, allocator);
        </#list>
    default:
        return ${types.anyHolder.name}(allocator);
    }
}
</#if>
<@namespace_end package.path/>
