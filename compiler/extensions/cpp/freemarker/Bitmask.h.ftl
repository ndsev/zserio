<#include "FileHeader.inc.ftl">
<#include "DocComment.inc.ftl">
<@file_header generatorDescription/>

<@include_guard_begin package.path, name/>

#include <zserio/BitStreamReader.h>
#include <zserio/BitStreamWriter.h>
#include <zserio/DeltaContext.h>
<#if !bitSize??>
#include <zserio/BitSizeOfCalculator.h>
</#if>
<#if withTypeInfoCode>
<@type_includes types.typeInfo/>
    <#if withReflectionCode>
<@type_includes types.reflectablePtr/>
    </#if>
</#if>
<@type_includes types.string/>
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
    using underlying_type = ${underlyingTypeInfo.typeFullName};

<#if withCodeComments>
    /** Enumeration of all bitmask values. */
</#if>
    enum class Values : underlying_type
    {
<#list values as value>
    <#if withCodeComments && value.docComments??>
        <@doc_comments value.docComments, 2/>
    </#if>
        ${value.name} = ${value.value}<#if !value?is_last>,</#if>
</#list>
    };

<#if withCodeComments>
    /** Default constructor. */
</#if>
    constexpr ${name}() noexcept :
            m_value(0)
    {}

<#if withCodeComments>
    /**
     * Read constructor.
     *
     * \param in Bit stream reader to use.
     */
</#if>
    explicit ${name}(::zserio::BitStreamReader& in);
<#if withCodeComments>

    /**
     * Read constructor.
     *
     * Called only internally if packed arrays are used.
     *
     * \param context Context for packed arrays.
     * \param in Bit stream reader to use.
     */
</#if>
    ${name}(::zserio::DeltaContext& context, ::zserio::BitStreamReader& in);
<#if withCodeComments>

    /**
     * Bitmask value constructor.
     *
     * \param value Bitmask value to construct from.
     */
</#if>
    constexpr ${name}(Values value) noexcept :
            m_value(static_cast<underlying_type>(value))
    {}

<#if withCodeComments>
    /**
     * Raw bitmask value constructor.
     *
     * \param value Raw bitmask value to construct from.
     */
</#if>
<#if upperBound??>
    explicit ${name}(underlying_type value);
<#else>
    constexpr explicit ${name}(underlying_type value) noexcept :
            m_value(value)
    {}
</#if>

<#if withCodeComments>
    /** Default destructor. */
</#if>
    ~${name}() = default;

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
<#if withTypeInfoCode>

    <#if withCodeComments>
    /**
     * Gets static information about this bitmask useful for generic introspection.
     *
     * \return Const reference to Zserio type information.
     */
    </#if>
    static const ${types.typeInfo.name}& typeInfo();
    <#if withReflectionCode>
        <#if withCodeComments>

    /**
     * Gets const reflection interface for this bitmask.
     *
     * \param allocator Allocator to use for all dynamic memory allocations.
     *
     * \return Const reference to Zserio type reflection.
     */
        </#if>
    ${types.reflectablePtr.name} reflectable(<#rt>
            <#lt>const ${types.allocator.default}& allocator = ${types.allocator.default}()) const;
    </#if>
</#if>

<#if withCodeComments>
    /**
     * Defines conversion to bitmask underlying type.
     *
     * \return Raw value which holds this bitmask.
     */
</#if>
    constexpr explicit operator underlying_type() const
    {
        return m_value;
    }

<#if withCodeComments>
    /**
     * Gets the bitmask raw value.
     *
     * \return Raw value which holds this bitmask.
     */
</#if>
    constexpr underlying_type getValue() const
    {
        return m_value;
    }

<#if withCodeComments>

    /**
     * Initializes context for packed arrays.
     *
     * Called only internally if packed arrays are used.
     *
     * \param context Context for packed arrays.
     */
</#if>
    void initPackingContext(::zserio::DeltaContext& context) const;

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
     * \param context Context for packed arrays.
     * \param bitPosition Bit stream position calculated from zero where the object will be serialized.
     *
     * \return Number of bits which are needed to store serialized object.
     */
</#if>
    size_t bitSizeOf(::zserio::DeltaContext& context, size_t bitPosition) const;
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
    size_t initializeOffsets(size_t bitPosition = 0) const;
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
    size_t initializeOffsets(::zserio::DeltaContext& context, size_t bitPosition) const;
</#if>

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
     * \param context Context for packed arrays.
     * \param out Bit stream writer where to serialize this Zserio object.
     */
    </#if>
    void write(::zserio::DeltaContext& context, ::zserio::BitStreamWriter& out) const;
</#if>

<#if withCodeComments>
    /**
     * Converts the bitmask value to the string.
     *
     * \param allocator Allocator to use for all dynamic memory allocations.
     *
     * \return String which represents this bitmask.
     */
</#if>
    ${types.string.name} toString(const ${types.string.name}::allocator_type& allocator =
            ${types.string.name}::allocator_type()) const;

private:
<#if underlyingTypeInfo.arrayTraits.isTemplated && underlyingTypeInfo.arrayTraits.requiresElementDynamicBitSize>
    class ZserioElementBitSize
    {
    public:
        static uint8_t get();
    };

</#if>
    static underlying_type readValue(::zserio::BitStreamReader& in);
    static underlying_type readValue(::zserio::DeltaContext& context, ::zserio::BitStreamReader& in);

    underlying_type m_value;
};

<#if withCodeComments>
/**
 * Defines operator '==' for the bitmask '${name}'.
 *
 * \param lhs Left operand.
 * \param rhs Right operand.
 *
 * \return True if lhs is equal to the rhs, otherwise false.
 */
</#if>
inline bool operator==(const ${name}& lhs, const ${name}& rhs)
{
    return lhs.getValue() == rhs.getValue();
}

<#if withCodeComments>
/**
 * Defines operator '!=' for the bitmask '${name}'.
 *
 * \param lhs Left operand.
 * \param rhs Right operand.
 *
 * \return True if lhs is not equal to the rhs, otherwise false.
 */
</#if>
inline bool operator!=(const ${name}& lhs, const ${name}& rhs)
{
    return lhs.getValue() != rhs.getValue();
}

<#if withCodeComments>
/**
 * Defines operator '|' for the bitmask '${name}'.
 *
 * \param lhs Left operand.
 * \param rhs Right operand.
 *
 * \return Bitmask which contains result after applying the operator '|' on given operands.
 */
</#if>
inline ${name} operator|(${name}::Values lhs, ${name}::Values rhs)
{
    return ${name}(static_cast<${name}::underlying_type>(lhs) | static_cast<${name}::underlying_type>(rhs));
}

<#if withCodeComments>
/**
 * Defines operator '|' for the bitmask '${name}'.
 *
 * \param lhs Const reference to the left operand.
 * \param rhs Const reference to the right operand.
 *
 * \return Bitmask which contains result after applying the operator '|' on given operands.
 */
</#if>
inline ${name} operator|(const ${name}& lhs, const ${name}& rhs)
{
    return ${name}(lhs.getValue() | rhs.getValue());
}

<#if withCodeComments>
/**
 * Defines operator '&' for the bitmask '${name}'.
 *
 * \param lhs Left operand.
 * \param rhs Right operand.
 *
 * \return Bitmask which contains result after applying the operator '&' on given operands.
 */
</#if>
inline ${name} operator&(${name}::Values lhs, ${name}::Values rhs)
{
    return ${name}(static_cast<${name}::underlying_type>(lhs) & static_cast<${name}::underlying_type>(rhs));
}

<#if withCodeComments>
/**
 * Defines operator '&' for the bitmask '${name}'.
 *
 * \param lhs Const reference to the left operand.
 * \param rhs Const reference to the right operand.
 *
 * \return Bitmask which contains result after applying the operator '&' on given operands.
 */
</#if>
inline ${name} operator&(const ${name}& lhs, const ${name}& rhs)
{
    return ${name}(lhs.getValue() & rhs.getValue());
}

<#if withCodeComments>
/**
 * Defines operator '^' for the bitmask '${name}'.
 *
 * \param lhs Left operand.
 * \param rhs Right operand.
 *
 * \return Bitmask which contains result after applying the operator '^' on given operands.
 */
</#if>
inline ${name} operator^(${name}::Values lhs, ${name}::Values rhs)
{
    return ${name}(static_cast<${name}::underlying_type>(lhs) ^ static_cast<${name}::underlying_type>(rhs));
}

<#if withCodeComments>
/**
 * Defines operator '^' for the bitmask '${name}'.
 *
 * \param lhs Const reference to the left operand.
 * \param rhs Const reference to the right operand.
 *
 * \return Bitmask which contains result after applying the operator '^' on given operands.
 */
</#if>
inline ${name} operator^(const ${name}& lhs, const ${name}& rhs)
{
    return ${name}(lhs.getValue() ^ rhs.getValue());
}

<#if withCodeComments>
/**
 * Defines operator '~' for the bitmask '${name}'.
 *
 * \param lhs Left operand.
 *
 * \return Bitmask which contains result after applying the operator '~' on given operand.
 */
</#if>
inline ${name} operator~(${name}::Values lhs)
{
    return ${name}(~static_cast<${name}::underlying_type>(lhs)<#if upperBound??> & ${upperBound}</#if>);
}

<#if withCodeComments>
/**
 * Defines operator '~' for the bitmask '${name}'.
 *
 * \param lhs Const reference to the left operand.
 *
 * \return Bitmask which contains result after applying the operator '~' on given operand.
 */
</#if>
inline ${name} operator~(const ${name}& lhs)
{
    return ${name}(~lhs.getValue()<#if upperBound??> & ${upperBound}</#if>);
}

<#if withCodeComments>
/**
 * Defines operator '|=' for the bitmask '${name}'.
 *
 * \param lhs Left operand.
 * \param rhs Right operand.
 *
 * \return Bitmask which contains result after applying the operator '|=' on given operands.
 */
</#if>
inline ${name} operator|=(${name}& lhs, const ${name}& rhs)
{
    lhs = ${name}(lhs.getValue() | rhs.getValue());
    return lhs;
}

<#if withCodeComments>
/**
 * Defines operator '&=' for the bitmask '${name}'.
 *
 * \param lhs Left operand.
 * \param rhs Right operand.
 *
 * \return Bitmask which contains result after applying the operator '&=' on given operands.
 */
</#if>
inline ${name} operator&=(${name}& lhs, const ${name}& rhs)
{
    lhs = ${name}(lhs.getValue() & rhs.getValue());
    return lhs;
}

<#if withCodeComments>
/**
 * Defines operator '^=' for the bitmask '${name}'.
 *
 * \param lhs Left operand.
 * \param rhs Right operand.
 *
 * \return Bitmask which contains result after applying the operator '^=' on given operands.
 */
</#if>
inline ${name} operator^=(${name}& lhs, const ${name}& rhs)
{
    lhs = ${name}(lhs.getValue() ^ rhs.getValue());
    return lhs;
}
<@namespace_end package.path/>

<@include_guard_end package.path, name/>
