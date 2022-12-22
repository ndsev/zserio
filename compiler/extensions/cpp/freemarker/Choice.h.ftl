<#include "FileHeader.inc.ftl">
<#include "CompoundConstructor.inc.ftl">
<#include "CompoundParameter.inc.ftl">
<#include "CompoundField.inc.ftl">
<#include "CompoundFunction.inc.ftl">
<#include "DocComment.inc.ftl">
<@file_header generatorDescription/>

<@include_guard_begin package.path, name/>

<@pybind_includes/>
<#if withWriterCode && fieldList?has_content>
#include <zserio/Traits.h>
</#if>
#include <zserio/BitStreamReader.h>
#include <zserio/BitStreamWriter.h>
#include <zserio/AllocatorPropagatingCopy.h>
<#if withTypeInfoCode>
<@type_includes types.typeInfo/>
    <#if withReflectionCode>
<@type_includes types.reflectablePtr/>
    </#if>
</#if>
<@type_includes types.anyHolder/>
<@type_includes types.allocator/>
<@type_includes types.packingContextNode/>
<@system_includes headerSystemIncludes/>
<@user_includes headerUserIncludes/>
<@namespace_begin package.path/>

<#if withCodeComments && docComments??>
<@doc_comments docComments/>
</#if>
class ${name}
{
<@top_private_section_declarations name, fieldList/>
public:
    static void zserioInitPyBind11(::pybind11::module m);

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
<#if withWriterCode>

    <@compound_default_constructor compoundConstructorsData/>

    <@compound_constructor_declaration compoundConstructorsData/>
</#if>

    <@compound_read_constructor_declaration compoundConstructorsData/>
<#if withCodeComments>

</#if>
    <@compound_read_constructor_declaration compoundConstructorsData, true/>

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

    <@compound_allocator_propagating_copy_constructor_declaration compoundConstructorsData/>
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

<#if withCodeComments>
    /**
     * Creates context for packed arrays.
     *
     * Called only internally if packed arrays are used.
     *
     * \param contextNode Context for packed arrays.
     */
</#if>
    static void createPackingContext(${types.packingContextNode.name}& contextNode);
<#if withCodeComments>

    /**
     * Initializes context for packed arrays.
     *
     * Called only internally if packed arrays are used.
     *
     * \param contextNode Context for packed arrays.
     */
</#if>
    void initPackingContext(${types.packingContextNode.name}& contextNode) const;

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
<#if withCodeComments>

    /**
     * Calculates size of the serialized object in bits for packed arrays.
     *
     * Called only internally if packed arrays are used.
     *
     * \param contextNode Context for packed arrays.
     * \param bitPosition Bit stream position calculated from zero where the object will be serialized.
     *
     * \return Number of bits which are needed to store serialized object.
     */
</#if>
    size_t bitSizeOf(${types.packingContextNode.name}& contextNode, size_t bitPosition) const;
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
    <#if withCodeComments>

    /**
     * Initializes offsets in this Zserio type and in all its fields for packed arrays.
     *
     * This method sets offsets in this Zserio type and in all fields recursively.
     * Called only internally if packed arrays are used.
     *
     * \param contextNode Context for packed arrays.
     * \param bitPosition Bit stream position calculated from zero where the object will be serialized.
     *
     * \return Bit stream position calculated from zero updated to the first byte after serialized object.
     */
    </#if>
    size_t initializeOffsets(${types.packingContextNode.name}& contextNode, size_t bitPosition);
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
    <#if withCodeComments>

    /**
     * Serializes this Zserio object to the bit stream for packed arrays.
     *
     * Called only internally if packed arrays are used.
     *
     * \param contextNode Context for packed arrays.
     * \param out Bit stream writer where to serialize this Zserio object.
     */
    </#if>
    void write(${types.packingContextNode.name}& contextNode, ::zserio::BitStreamWriter& out) const;
</#if>

private:
<#if fieldList?has_content>
    ${types.anyHolder.name} readObject(::zserio::BitStreamReader& in, const allocator_type& allocator);
    ${types.anyHolder.name} readObject(${types.packingContextNode.name}& contextNode,
            ::zserio::BitStreamReader& in, const allocator_type& allocator);
    ${types.anyHolder.name} copyObject(const allocator_type& allocator) const;

</#if>
    <@compound_parameter_members compoundParametersData/>
    <@compound_constructor_members compoundConstructorsData/>
<#if fieldList?has_content>
    ${types.anyHolder.name} m_objectChoice;
</#if>
};
<@namespace_end package.path/>

<@include_guard_end package.path, name/>
