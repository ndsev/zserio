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
<@type_includes types.allocator/>
<#if has_optional_field(fieldList)>
    <#if has_optional_recursive_field(fieldList)>
<@type_includes types.heapOptionalHolder/>
    </#if>
    <#if has_optional_non_recursive_field(fieldList)>
<@type_includes types.inplaceOptionalHolder/>
    </#if>
</#if>
<@system_includes headerSystemIncludes/>
<@user_includes headerUserIncludes/>
<@system_includes cppSystemIncludes/>
<@user_includes cppUserIncludes/>
<@namespace_begin package.path/>

<#assign numExtendedFields=num_extended_fields(fieldList)>
<#if withCodeComments && docComments??>
<@doc_comments docComments/>
</#if>
class ${name}
{
<#--
public:
<#if isPackable && usedInPackedArray>
    <@compound_declare_packing_context fieldList/>

</#if>
<#if withCodeComments>
    /** Definition for allocator type. */
</#if>
    using allocator_type = ${types.allocator.default};
<#if withWriterCode>

    <@compound_default_constructor compoundConstructorsData/>

    <@compound_constructor_declaration compoundConstructorsData/>
    <#if fieldList?has_content>

        <#if withCodeComments>
    /**
     * Fields constructor.
     *
            <#list compoundConstructorsData.fieldList as field>
     * \param <@field_argument_name field/> Value of the field \ref ${field.getterName} "${field.name}".
            </#list>
     * \param allocator Allocator to construct from.
     */
        </#if>
    <@compound_field_constructor_template_arg_list compoundConstructorsData.compoundName,
            compoundConstructorsData.fieldList/>
    <#if compoundConstructorsData.fieldList?size == 1>explicit </#if>${compoundConstructorsData.compoundName}(
            <#lt><@compound_field_constructor_type_list compoundConstructorsData.fieldList, 3/>,
            const allocator_type& allocator = allocator_type()) :
            ${compoundConstructorsData.compoundName}(allocator)
    {
        <#list compoundConstructorsData.fieldList as field>
        <@field_member_name field/> = <#rt>
            <#if !field.typeInfo.isSimple || field.optional??>
                <#lt><@compound_setter_field_forward_value field/>;
            <#else>
                <#lt><@compound_setter_field_value field/>;
            </#if>
        </#list>
    }
    </#if>
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
    <@compound_parameter_accessors_declaration compoundParametersData/>
<#list fieldList as field>

    <@compound_field_accessors_declaration field/>
    <#if field.isExtended>

        <#if withCodeComments>
    /**
     * Checks if the extended field ${field.name} is present.
     *
     * \return True if the extended field ${field.name} is present, otherwise false.
     */
        </#if>
    bool ${field.isPresentIndicatorName}() const;
    </#if>
    <#if field.optional??>
        <#if withCodeComments>

    /**
     * Checks if the optional field ${field.name} is used during serialization and deserialization.
     *
     * \return True if the optional field ${field.name} is used, otherwise false.
     */
        </#if>
    bool ${field.optional.isUsedIndicatorName}() const;
        <#if withWriterCode>
            <#if withCodeComments>

    /**
     * Checks if the optional field ${field.name} is set.
     *
     * \return True if the optional field ${field.name} is set, otherwise false.
     */
            </#if>
    bool ${field.optional.isSetIndicatorName}() const;
            <#if withCodeComments>

    /**
     * Resets the optional field ${field.name}.
     */
            </#if>
    void ${field.optional.resetterName}();
        </#if>
    </#if>
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

private:
    <@private_section_declarations name, fieldList/>
<#list fieldList as field>
    <@field_member_type_name field/> ${field.readerName}(::zserio::BitStreamReader& in<#rt>
    <#if field.needsAllocator || field.holderNeedsAllocator>
            <#lt>,
            const allocator_type& allocator<#rt>
    </#if>
    <#lt>);
    <#if field.isPackable && usedInPackedArray>
    <@field_member_type_name field/> ${field.readerName}(ZserioPackingContext& context,
            ::zserio::BitStreamReader& in<#rt>
        <#if field.needsAllocator || field.holderNeedsAllocator>
            , const allocator_type& allocator<#t>
        </#if>
            <#lt>);
    </#if>
    <#if !field?has_next>

    </#if>
</#list>
    <@compound_parameter_members compoundParametersData/>
    <@compound_constructor_members compoundConstructorsData/>
<#if (numExtendedFields > 0)>
    uint32_t m_numExtendedFields;
</#if>
<#list fieldList as field>
    <@field_member_type_name field/> <@field_member_name field/>;
</#list>
-->
<#function compound_field_default_constructor_arguments fieldList>
    <#local arguments=[]/>
    <#list fieldList as field>
        <#if field.optional??>
            <#if field.holderNeedsAllocator>
                <#local argument>${field.name}(allocator)</#local>
                <#local arguments+=[argument]/>
            </#if>
        <#elseif field.array??>
            <#local argument>${field.name}(allocator)</#local>
            <#local arguments+=[argument]/>
        <#elseif field.needsAllocator>
            <#local argument>${field.name}(allocator)</#local>
            <#local arguments+=[argument]/>
        </#if>
    </#list>
    <#return arguments/>
</#function>
public:
    using allocator_type = ${types.allocator.default};
    struct Storage;
    class View;

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

<#assign storageDefaultConstructorArguments=compound_field_default_constructor_arguments(fieldList)/>
        explicit Storage(const allocator_type&<#if storageDefaultConstructorArguments?has_content> allocator) :<#else>)</#if>
<#list storageDefaultConstructorArguments as argument>
                ${argument}<#if argument?has_next>,</#if>
</#list>
        {}
<#if fieldList?has_content>

        template <
    <#list fieldList as field>
                typename ZSERIO_TYPE_${field.name},
    </#list>
                typename std::enable_if<!std::is_same<ZSERIO_TYPE_${fieldList[0].name}, allocator_type>::value, int>::type = 0>
        Storage(
    <#list fieldList as field>
                ZSERIO_TYPE_${field.name}&& <@field_argument_name field/>,
    </#list>
                const allocator_type& allocator = allocator_type()) :
        <#list fieldList as field>
                ${field.name}(std::forward<ZSERIO_TYPE_${field.name}>(<@field_argument_name field/>)<#if (field.optional?? && field.holderNeedsAllocator) || (!field.optional?? && field.array??)>, allocator</#if>)<#if field?has_next>,<#else></#if>
        </#list>
        {}
</#if>

        <#-- TODO[Mi-L@]: What about allocator extended copy/move constructors? -->
<#list fieldList as field>
        <@field_storage_type field/> ${field.name};
</#list>
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
<#list fieldList as field>
            // ${field.name}
            <@field_view_read field, 3/>
    <#if field?has_next>

    </#if>
</#list>
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
    <#list fieldList as field>
            // ${field.name}
            <@field_view_read field, 3, true/>
        <#if field?has_next>

        </#if>
    </#list>
        }
</#if>
        <@compound_parameter_view_accessors compoundParametersData/>
<#list fieldList as field>
    <#if field.optional??>

        bool ${field.optional.isSetIndicatorName}() const
        {
            return m_storage.${field.name}.hasValue();
        }

        bool ${field.optional.isUsedIndicatorName}() const
        {
            return (<@field_optional_condition field/>);
        }
    </#if>

        <@field_view_type field/> ${field.getterName}() const
        {
    <#if field.optional??>
            if (!${field.optional.isUsedIndicatorName}())
            {
                throw ::zserio::CppRuntimeException("${name}: optional field '${field.name}' is not used!");
            }

    </#if>
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
