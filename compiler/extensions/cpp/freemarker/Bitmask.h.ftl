<#include "FileHeader.inc.ftl">
<@file_header generatorDescription/>

<@include_guard_begin package.path, name/>

#include <zserio/BitStreamReader.h>
#include <zserio/BitStreamWriter.h>
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
<@type_includes types.packingContextNode/>
<@system_includes headerSystemIncludes/>
<@user_includes headerUserIncludes/>
<@namespace_begin package.path/>

class ${name}
{
public:
    typedef ${underlyingTypeInfo.typeFullName} underlying_type;

    enum class Values : underlying_type
    {
<#list values as value>
        ${value.name} = ${value.value}<#if !value?is_last>,</#if>
</#list>
    };

    constexpr ${name}() noexcept :
        m_value(0)
    {}

    explicit ${name}(::zserio::BitStreamReader& in);
    ${name}(${types.packingContextNode.name}& contextNode, ::zserio::BitStreamReader& in);
    constexpr ${name}(Values value) noexcept :
        m_value(static_cast<underlying_type>(value))
    {}

    <#if upperBound??>
    explicit ${name}(underlying_type value);
    <#else>
    constexpr explicit ${name}(underlying_type value) noexcept :
        m_value(value)
    {}
    </#if>

    ~${name}() = default;

    ${name}(const ${name}&) = default;
    ${name}& operator=(const ${name}&) = default;

    ${name}(${name}&&) = default;
    ${name}& operator=(${name}&&) = default;
<#if withTypeInfoCode>

    static const ${types.typeInfo.name}& typeInfo();
    <#if withReflectionCode>
    ${types.reflectablePtr.name} reflectable(<#rt>
            <#lt>const ${types.allocator.default}& allocator = ${types.allocator.default}()) const;
    </#if>
</#if>

    constexpr explicit operator underlying_type() const
    {
        return m_value;
    }

    constexpr underlying_type getValue() const
    {
        return m_value;
    }

    static void createPackingContext(${types.packingContextNode.name}& contextNode);
    void initPackingContext(${types.packingContextNode.name}& contextNode) const;

    size_t bitSizeOf(size_t bitPosition = 0) const;
    size_t bitSizeOf(${types.packingContextNode.name}& contextNode, size_t bitPosition) const;
<#if withWriterCode>

    size_t initializeOffsets(size_t bitPosition = 0) const;
    size_t initializeOffsets(${types.packingContextNode.name}& contextNode, size_t bitPosition) const;
</#if>

    uint32_t hashCode() const;
<#if withWriterCode>

    void write(::zserio::BitStreamWriter& out) const;
    void write(${types.packingContextNode.name}& contextNode, ::zserio::BitStreamWriter& out) const;
</#if>

    ${types.string.name} toString(const ${types.string.name}::allocator_type& allocator =
            ${types.string.name}::allocator_type()) const;

private:
    static underlying_type readValue(::zserio::BitStreamReader& in);
    static underlying_type readValue(${types.packingContextNode.name}& contextNode,
            ::zserio::BitStreamReader& in);

    underlying_type m_value;
};

inline bool operator==(const ${name}& lhs, const ${name}& rhs)
{
    return lhs.getValue() == rhs.getValue();
}

inline bool operator!=(const ${name}& lhs, const ${name}& rhs)
{
    return lhs.getValue() != rhs.getValue();
}

inline ${name} operator|(${name}::Values lhs, ${name}::Values rhs)
{
    return ${name}(static_cast<${name}::underlying_type>(lhs) | static_cast<${name}::underlying_type>(rhs));
}

inline ${name} operator|(const ${name}& lhs, const ${name}& rhs)
{
    return ${name}(lhs.getValue() | rhs.getValue());
}

inline ${name} operator&(${name}::Values lhs, ${name}::Values rhs)
{
    return ${name}(static_cast<${name}::underlying_type>(lhs) & static_cast<${name}::underlying_type>(rhs));
}

inline ${name} operator&(const ${name}& lhs, const ${name}& rhs)
{
    return ${name}(lhs.getValue() & rhs.getValue());
}

inline ${name} operator^(${name}::Values lhs, ${name}::Values rhs)
{
    return ${name}(static_cast<${name}::underlying_type>(lhs) ^ static_cast<${name}::underlying_type>(rhs));
}

inline ${name} operator^(const ${name}& lhs, const ${name}& rhs)
{
    return ${name}(lhs.getValue() ^ rhs.getValue());
}

inline ${name} operator~(${name}::Values lhs)
{
    return ${name}(~static_cast<${name}::underlying_type>(lhs)<#if upperBound??> & ${upperBound}</#if>);
}

inline ${name} operator~(const ${name}& lhs)
{
    return ${name}(~lhs.getValue()<#if upperBound??> & ${upperBound}</#if>);
}

inline ${name} operator|=(${name}& lhs, const ${name}& rhs)
{
    lhs = ${name}(lhs.getValue() | rhs.getValue());
    return lhs;
}

inline ${name} operator&=(${name}& lhs, const ${name}& rhs)
{
    lhs = ${name}(lhs.getValue() & rhs.getValue());
    return lhs;
}

inline ${name} operator^=(${name}& lhs, const ${name}& rhs)
{
    lhs = ${name}(lhs.getValue() ^ rhs.getValue());
    return lhs;
}
<@namespace_end package.path/>

<@include_guard_end package.path, name/>
