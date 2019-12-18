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

    struct Values
    {
<#list values as value>
        static const ${name} ${value.name};
</#list>
    };

    ${name}() noexcept;
    explicit ${name}(::zserio::BitStreamReader& in);
    explicit ${name}(underlying_type value) noexcept;
    
    ${name}(const ${name}&) = default;
    ${name}& operator=(const ${name}&) = default;

    ${name}(${name}&&) = default;
    ${name}& operator=(${name}&&) = default;

    underlying_type getValue() const;

    size_t bitSizeOf(size_t bitPosition = 0) const;
<#if withWriterCode>
    size_t initializeOffsets(size_t bitPosition) const;
</#if>

    bool operator==(const ${name}& other) const;
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

inline ${name} operator|(const ${name}& lhs, const ${name}& rhs)
{
    return ${name}(lhs.getValue() | rhs.getValue());
}

inline ${name} operator&(const ${name}& lhs, const ${name}& rhs)
{
    return ${name}(lhs.getValue() & rhs.getValue());
}

inline ${name} operator^(const ${name}& lhs, const ${name}& rhs)
{
    return ${name}(lhs.getValue() ^ rhs.getValue());
}

inline ${name} operator~(const ${name}& lhs)
{
    return ${name}(~lhs.getValue());
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
