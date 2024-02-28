<#include "FileHeader.inc.ftl">
<#include "CompoundConstructor.inc.ftl">
<#include "CompoundParameter.inc.ftl">
<#include "CompoundField.inc.ftl">
<#include "CompoundFunction.inc.ftl">
<#include "DocComment.inc.ftl">
<@file_header generatorDescription/>

<@include_guard_begin package.path, name/>

<@runtime_version_check generatorVersion/>

<#if withWriterCode && fieldList?has_content>
#include <zserio/Traits.h>
</#if>
<#if needs_compound_initialization(compoundConstructorsData)>
#include <zserio/NoInit.h>
</#if>
#include <zserio/BitStreamReader.h>
#include <zserio/BitStreamWriter.h>
#include <zserio/AllocatorPropagatingCopy.h>
<#if isPackable && usedInPackedArray>
#include <zserio/DeltaContext.h>
</#if>
<#if withTypeInfoCode>
<@type_includes types.typeInfo/>
    <#if withReflectionCode>
<@type_includes types.reflectablePtr/>
    </#if>
</#if>
<@type_includes types.anyHolder/>
<@type_includes types.allocator/>
<@system_includes headerSystemIncludes/>
<@system_includes cppSystemIncludes/>
<@user_includes headerUserIncludes/>
<@user_includes cppUserIncludes/>
<@namespace_begin package.path/>

<#if withCodeComments && docComments??>
<@doc_comments docComments/>
</#if>
class ${name}
{
<#--
public:
<#if withCodeComments>
    /** Definition for allocator type. */
</#if>
    using allocator_type = ${types.allocator.default};

<#if withCodeComments>
    /** Choice tag enumeration which denotes chosen field. */
</#if>
    enum ChoiceTag : int32_t
    {
<#list fieldList as field>
    <#if withCodeComments>
        /** Choice tag which denotes chosen field ${field.name}. */
    </#if>
        <@choice_tag_name field/> = ${field?index},
</#list>
<#if withCodeComments>
        /** Choice tag which is used if no field has been set yet. */
</#if>
        UNDEFINED_CHOICE = -1
    };
<#if isPackable && usedInPackedArray>

    <@compound_declare_packing_context fieldList/>
</#if>
<#if withWriterCode>

    <@compound_default_constructor compoundConstructorsData/>

    <@compound_constructor_declaration compoundConstructorsData/>
</#if>

    <@compound_read_constructor_declaration compoundConstructorsData/>
<#if isPackable && usedInPackedArray>
    <#if withCodeComments>

    </#if>
    <@compound_read_constructor_declaration compoundConstructorsData, true/>
</#if>

<#if withCodeComments>
    /** Default destructor. */
</#if>
    ~${name}() = default;
<#if needs_compound_initialization(compoundConstructorsData) || has_field_with_initialization(fieldList)>

    <@compound_copy_constructor_declaration compoundConstructorsData/>
    <#if withCodeComments>

    </#if>
    <@compound_assignment_operator_declaration compoundConstructorsData/>

    <@compound_move_constructor_declaration compoundConstructorsData/>
    <#if withCodeComments>

    </#if>
    <@compound_move_assignment_operator_declaration compoundConstructorsData/>
<#else>

    <#if withCodeComments>
    /** Default copy constructor. */
    </#if>
    ${name}(const ${name}&) = default;
    <#if withCodeComments>
    /** Default assignment operator. */
    </#if>
    ${name}& operator=(const ${name}&) = default;

    <#if withCodeComments>
    /** Default move constructor. */
    </#if>
    ${name}(${name}&&) = default;
    <#if withCodeComments>
    /** Default move assignment operator. */
    </#if>
    ${name}& operator=(${name}&&) = default;
</#if>
<#if needs_compound_initialization(compoundConstructorsData)>

    <@compound_copy_constructor_no_init_declaration compoundConstructorsData/>
    <#if withCodeComments>

    </#if>
    <@compound_assignment_no_init_declaration compoundConstructorsData/>

    <@compound_move_constructor_no_init_declaration compoundConstructorsData/>
    <#if withCodeComments>

    </#if>
    <@compound_move_assignment_no_init_declaration compoundConstructorsData/>
</#if>

    <@compound_allocator_propagating_copy_constructor_declaration compoundConstructorsData/>
<#if needs_compound_initialization(compoundConstructorsData)>
    <#if withCodeComments>

    </#if>
    <@compound_allocator_propagating_copy_constructor_no_init_declaration compoundConstructorsData/>
</#if>
<#if withTypeInfoCode>

    <#if withCodeComments>
    /**
     * Gets static information about this Zserio type useful for generic introspection.
     *
     * \return Const reference to Zserio type information.
     */
    </#if>
    static const ${types.typeInfo.name}& typeInfo();
    <#if withReflectionCode>
        <#if withCodeComments>

    /**
     * Gets const reflection interface for this Zserio type.
     *
     * \param allocator Allocator to use for all dynamic memory allocations.
     *
     * \return Const reference to Zserio type reflection.
     */
        </#if>
    ${types.reflectableConstPtr.name} reflectable(const allocator_type& allocator = allocator_type()) const;
        <#if withWriterCode>
            <#if withCodeComments>

    /**
     * Gets reflection interface for this Zserio type.
     *
     * \param allocator Allocator to use for all dynamic memory allocations.
     *
     * \return Reference to Zserio type reflection.
     */
            </#if>
    ${types.reflectablePtr.name} reflectable(const allocator_type& allocator = allocator_type());
        </#if>
    </#if>
</#if>
<#if needs_compound_initialization(compoundConstructorsData) || needsChildrenInitialization>

    <#if needs_compound_initialization(compoundConstructorsData)>
    <@compound_initialize_declaration compoundConstructorsData/>
    </#if>
    <#if needsChildrenInitialization>
    <@compound_initialize_children_declaration/>
    </#if>
</#if>

<#if withCodeComments>
    /**
     * Gets the current choice tag.
     *
     * \return Choice tag which denotes chosen field.
     */
</#if>
    ChoiceTag choiceTag() const;
    <@compound_parameter_accessors_declaration compoundParametersData/>
<#list fieldList as field>

    <@compound_field_accessors_declaration field/>
</#list>
    <@compound_functions_declaration compoundFunctionsData/>
<#if isPackable && usedInPackedArray>
    <#if withCodeComments>

    /**
     * Initializes context for packed arrays.
     *
     * Called only internally if packed arrays are used.
     *
     * \param context Context for packed arrays.
     */
    </#if>
    void initPackingContext(ZserioPackingContext& context) const;
</#if>

<#if withCodeComments>
    /**
     * Calculates size of the serialized object in bits.
     *
     * \param bitPosition Bit stream position calculated from zero where the object will be serialized.
     *
     * \return Number of bits which are needed to store serialized object.
     */
</#if>
    size_t bitSizeOf(size_t bitPosition = 0) const;
<#if isPackable && usedInPackedArray>
    <#if withCodeComments>

    /**
     * Calculates size of the serialized object in bits for packed arrays.
     *
     * Called only internally if packed arrays are used.
     *
     * \param context Context for packed arrays.
     * \param bitPosition Bit stream position calculated from zero where the object will be serialized.
     *
     * \return Number of bits which are needed to store serialized object.
     */
    </#if>
    size_t bitSizeOf(ZserioPackingContext& context, size_t bitPosition) const;
</#if>
<#if withWriterCode>

    <#if withCodeComments>
    /**
     * Initializes offsets in this Zserio object and in all its fields.
     *
     * This method sets offsets in this Zserio object and in all fields recursively.
     *
     * \param bitPosition Bit stream position calculated from zero where the object will be serialized.
     *
     * \return Bit stream position calculated from zero updated to the first byte after serialized object.
     */
    </#if>
    size_t initializeOffsets(size_t bitPosition = 0);
    <#if isPackable && usedInPackedArray>
        <#if withCodeComments>

    /**
     * Initializes offsets in this Zserio type and in all its fields for packed arrays.
     *
     * This method sets offsets in this Zserio type and in all fields recursively.
     * Called only internally if packed arrays are used.
     *
     * \param context Context for packed arrays.
     * \param bitPosition Bit stream position calculated from zero where the object will be serialized.
     *
     * \return Bit stream position calculated from zero updated to the first byte after serialized object.
     */
        </#if>
    size_t initializeOffsets(ZserioPackingContext& context, size_t bitPosition);
    </#if>
</#if>

<#if withCodeComments>
    /**
     * Comparison operator.
     *
     * \param other Instance to compare.
     *
     * \return True if this and other instance is the same, otherwise false.
     */
</#if>
    bool operator==(const ${name}& other) const;

<#if withCodeComments>
    /**
     * Less than operator.
     *
     * \param other Instance to compare.
     *
     * \return True if this instance is less than the other instance, otherwise false.
     */
</#if>
    bool operator<(const ${name}& other) const;

<#if withCodeComments>
    /**
     * Calculates hash code of this Zserio object.
     *
     * \return Calculated hash code.
     */
</#if>
    uint32_t hashCode() const;
<#if withWriterCode>

    <#if withCodeComments>
    /**
     * Serializes this Zserio object to the bit stream.
     *
     * \param out Bit stream writer where to serialize this Zserio object.
     */
    </#if>
    void write(::zserio::BitStreamWriter& out) const;
    <#if isPackable && usedInPackedArray>
        <#if withCodeComments>

    /**
     * Serializes this Zserio object to the bit stream for packed arrays.
     *
     * Called only internally if packed arrays are used.
     *
     * \param context Context for packed arrays.
     * \param out Bit stream writer where to serialize this Zserio object.
     */
        </#if>
    void write(ZserioPackingContext& context, ::zserio::BitStreamWriter& out) const;
    </#if>
</#if>

private:
    <@private_section_declarations name, fieldList/>
<#if fieldList?has_content>
    ${types.anyHolder.name} readObject(::zserio::BitStreamReader& in, const allocator_type& allocator);
    <#if isPackable && usedInPackedArray>
    ${types.anyHolder.name} readObject(ZserioPackingContext& context, ::zserio::BitStreamReader& in,
            const allocator_type& allocator);
    </#if>
    ${types.anyHolder.name} copyObject(const allocator_type& allocator) const;

</#if>
    <@compound_parameter_members compoundParametersData/>
    <@compound_constructor_members compoundConstructorsData/>
<#if fieldList?has_content>
    ${types.anyHolder.name} m_objectChoice;
</#if>
-->
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
<#macro choice_field_view_read member packed indent>
    <#local I>${""?left_pad(indent * 4)}</#local>
    <#if member.compoundField??>
    <@field_view_read member.compoundField, indent, packed/>
    </#if>
${I}break;
</#macro>
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
public:
    using allocator_type = ${types.allocator.default};
    struct Storage;
    class View;

    enum ChoiceTag : int32_t
    {
<#list fieldList as field>
        <@choice_tag_name field/> = ${field?index},
</#list>
        UNDEFINED_CHOICE = -1
    };

<#if isPackable && usedInPackedArray>
    <@compound_declare_packing_context fieldList/>

</#if>
<#list fieldList as field>
    <#if needs_array_expressions(field)>
        <@declare_array_expressions name, field/>
    </#if>
    <#if needs_field_element_factory(field)>
        <@declare_element_factory name, field/>
    </#if>
    <#if needs_field_element_bit_size(field)>
        <@declare_element_bit_size name, field/>
    </#if>
</#list>
    <@arrays_typedefs fieldList/>
    struct Storage
    {
<#if isPackable && usedInPackedArray>
        using ZserioPackingContext = ${fullName}::ZserioPackingContext;

</#if>
        Storage() noexcept :
                Storage(allocator_type())
        {}

        explicit Storage(const allocator_type& allocator) noexcept :
                any(allocator)
        {}

        template <typename T,
                typename std::enable_if<!std::is_same<T, allocator_type>::value, int>::type = 0>
        explicit Storage(T&& value, const allocator_type& allocator = allocator_type()) :
                any(std::forward<T>(value), allocator)
        {}

        ${types.anyHolder.name} any;
    };

    class View
    {
    public:
        View(<#rt>
<#if compoundConstructorsData.compoundParametersData.list?has_content>
        <@compound_parameter_view_constructor_type_list compoundConstructorsData.compoundParametersData, 4/><#t>
                Storage& storage) noexcept :
<#else>
                <#lt>Storage& storage) noexcept :
</#if>
<#if compoundConstructorsData.compoundParametersData.list?has_content>
                <#lt><@compound_parameter_view_constructor_initializers compoundConstructorsData.compoundParametersData, 4, true/>
</#if>
                m_storage(storage)
        {}

        View(::zserio::BitStreamReader& reader, <#rt>
<#if compoundConstructorsData.compoundParametersData.list?has_content>
                <@compound_parameter_view_constructor_type_list compoundConstructorsData.compoundParametersData, 4/><#t>
                Storage& storage, const allocator_type& allocator = allocator_type()) :
<#else>
                <#lt>Storage& storage, const allocator_type& allocator = allocator_type()) :
</#if>
<#if compoundConstructorsData.compoundParametersData.list?has_content>
                <#lt><@compound_parameter_view_constructor_initializers compoundConstructorsData.compoundParametersData, 4, true/>
</#if>
                m_storage(storage)
        {
            <@choice_switch "choice_field_view_read", "choice_no_match", selectorExpression, 3/>
        }
<#if isPackable && usedInPackedArray>

        View(ZserioPackingContext& context, ::zserio::BitStreamReader& reader, <#rt>
    <#if compoundConstructorsData.compoundParametersData.list?has_content>
                <@compound_parameter_view_constructor_type_list compoundConstructorsData.compoundParametersData, 4/><#t>
                Storage& storage, const allocator_type& allocator = allocator_type()) :
    <#else>
                <#lt>Storage& storage, const allocator_type& allocator = allocator_type()) :
    </#if>
    <#if compoundConstructorsData.compoundParametersData.list?has_content>
                <#lt><@compound_parameter_view_constructor_initializers compoundConstructorsData.compoundParametersData, 4, true/>
    </#if>
                m_storage(storage)
        {
            <@choice_switch "choice_field_view_read", "choice_no_match", selectorExpression, 3, true/>
        }
</#if>

        ChoiceTag choiceTag() const
        {
            <@choice_switch "choice_tag_member", "choice_tag_no_match", selectorExpression, 3/>
        }
        <@compound_parameter_view_accessors compoundParametersData/>
<#list fieldList as field>

        <@field_view_type field/> ${field.getterName}() const
        {
            <@field_view_get field, 3/>
        }
</#list>

    private:
        <@compound_parameter_view_members compoundParametersData/>
        Storage& m_storage;
    };
};
<#list fieldList as field>
    <#if needs_array_expressions(field)>
<@define_array_expressions_methods name, field/>
    </#if>
    <#if needs_field_element_factory(field)>
<@define_element_factory_methods name, field/>
    </#if>
    <#if needs_field_element_bit_size(field)>
<@define_element_bit_size_methods name, field/>
    </#if>
</#list>
<@namespace_end package.path/>

<@include_guard_end package.path, name/>
