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
<#if !fieldList?has_content>
#include <zserio/Array.h>
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
<@user_includes headerUserIncludes/>
<@namespace_begin package.path/>

<#if withCodeComments && docComments??>
<@doc_comments docComments/>
</#if>
class ${name}
{
public:
    <#if withCodeComments>
    /** Definition for allocator type. */
    </#if>
    using allocator_type = ${types.allocator.default};

    <#if withCodeComments>
    /** Choice tag enumeration which denotes chosen union field. */
    </#if>
    enum ChoiceTag : int32_t
    {
<#list fieldList as field>
        <@choice_tag_name field/> = ${field?index},
</#list>
        UNDEFINED_CHOICE = -1
    };
<#if isPackable && usedInPackedArray>

    <@compound_declare_packing_context fieldList, true/>
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
     * \return Choice tag which denotes chosen union field.
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
     * \return True if this instance is less than other instance, otherwise false.
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
<#if withBitPositionCode>

    <#if withCodeComments>
    /**
     * Get the bit position in the parsed blob after reading.
     *
     * This feature is experimental and can be removed without any warning!
     *
     * \note Note that the returned bit position is valid only directly after read! If the Zserio object
     *       has been changed after reading, the result is unspecified!
     *
     * \return Reader bit position in the parsed blob counted from zero.
     */
    </#if>
    size_t bitPosition() const;
</#if>

private:
    <@private_section_declarations name, fieldList/>
<#if fieldList?has_content>
    ChoiceTag readChoiceTag(::zserio::BitStreamReader& in);
    <#if isPackable && usedInPackedArray>
    ChoiceTag readChoiceTag(ZserioPackingContext& context, ::zserio::BitStreamReader& in);
    </#if>
    ${types.anyHolder.name} readObject(::zserio::BitStreamReader& in, const allocator_type& allocator);
    <#if isPackable && usedInPackedArray>
    ${types.anyHolder.name} readObject(ZserioPackingContext& context, ::zserio::BitStreamReader& in,
            const allocator_type& allocator);
    </#if>
    ${types.anyHolder.name} copyObject(const allocator_type& allocator) const;

</#if>
    <@compound_parameter_members compoundParametersData/>
    <@compound_constructor_members compoundConstructorsData/>
<#if withBitPositionCode>
    <#-- Bit position must be before m_choiceTag and m_objectChoice in order to get initialized first. -->
    size_t m_bitPosition;
</#if>
    ChoiceTag m_choiceTag;
<#if fieldList?has_content>
    ${types.anyHolder.name} m_objectChoice;
</#if>
};
<@namespace_end package.path/>

<@include_guard_end package.path, name/>
