<#include "FileHeader.inc.ftl">
<@file_header generatorDescription/>

<@include_guard_begin package.path, name/>

#include <array>

#include <zserio/BitStreamReader.h>
#include <zserio/BitStreamWriter.h>
#include <zserio/PreWriteAction.h>
<#if !bitSize??>
#include <zserio/BitSizeOfCalculator.h>
</#if>
<@system_includes headerSystemIncludes/>
<@user_includes headerUserIncludes, true/>
<@namespace_begin package.path/>

class ${name}
{
public:
    typedef ${baseCppTypeName} underlying_type;

    enum class Values : underlying_type
    {
<#list values as value>
        ${value.name} = ${value.value}<#if !value?is_last>,</#if>
</#list>
    };

    constexpr ${name}() noexcept :
        m_value(0)
    {
    }
    
    explicit ${name}(::zserio::BitStreamReader& in);
    constexpr ${name}(Values value) noexcept :
        m_value(static_cast<underlying_type>(value))
    {
    }

    constexpr explicit ${name}(underlying_type value) noexcept :
        m_value(value)
    {
    }

    ~${name}() = default;

    ${name}(const ${name}&) = default;
    ${name}& operator=(const ${name}&) = default;

    ${name}(${name}&&) = default;
    ${name}& operator=(${name}&&) = default;

    explicit operator underlying_type() const;
    underlying_type getValue() const;

    size_t bitSizeOf(size_t bitPosition = 0) const;
<#if withWriterCode>
    size_t initializeOffsets(size_t bitPosition) const;
</#if>

    int hashCode() const;

    void read(::zserio::BitStreamReader& in);
<#if withWriterCode>
    void write(::zserio::BitStreamWriter& out,
            ::zserio::PreWriteAction preWriteAction = ::zserio::ALL_PRE_WRITE_ACTIONS) const;
</#if>

    std::string toString() const;

private:
    static underlying_type readValue(::zserio::BitStreamReader& in);

    underlying_type m_value;
};

inline bool operator==(const ${name}::Values& lhs, const ${name}::Values& rhs)
{
    return static_cast<${name}::underlying_type>(lhs) == static_cast<${name}::underlying_type>(rhs);
}

inline bool operator==(const ${name}& lhs, const ${name}& rhs)
{
    return lhs.getValue() == rhs.getValue();
}

inline bool operator!=(const ${name}::Values& lhs, const ${name}::Values& rhs)
{
    return static_cast<${name}::underlying_type>(lhs) != static_cast<${name}::underlying_type>(rhs);
}

inline bool operator!=(const ${name}& lhs, const ${name}& rhs)
{
    return lhs.getValue() != rhs.getValue();
}

inline ${name} operator|(const ${name}::Values& lhs, const ${name}::Values& rhs)
{
    return ${name}(static_cast<${name}::underlying_type>(lhs) | static_cast<${name}::underlying_type>(rhs));
}

inline ${name} operator|(const ${name}& lhs, const ${name}& rhs)
{
    return ${name}(lhs.getValue() | rhs.getValue());
}

inline ${name} operator&(const ${name}::Values& lhs, const ${name}::Values& rhs)
{
    return ${name}(static_cast<${name}::underlying_type>(lhs) & static_cast<${name}::underlying_type>(rhs));
}

inline ${name} operator&(const ${name}& lhs, const ${name}& rhs)
{
    return ${name}(lhs.getValue() & rhs.getValue());
}

inline ${name} operator^(const ${name}::Values& lhs, const ${name}::Values& rhs)
{
    return ${name}(static_cast<${name}::underlying_type>(lhs) ^ static_cast<${name}::underlying_type>(rhs));
}

inline ${name} operator^(const ${name}& lhs, const ${name}& rhs)
{
    return ${name}(lhs.getValue() ^ rhs.getValue());
}

inline ${name} operator~(const ${name}::Values& lhs)
{
    return ${name}(~static_cast<${name}::underlying_type>(lhs) & ((1 << ${runtimeFunction.arg}) - 1));
}

inline ${name} operator~(const ${name}& lhs)
{
    return ${name}(~lhs.getValue() & ((1 << ${runtimeFunction.arg}) - 1));
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
