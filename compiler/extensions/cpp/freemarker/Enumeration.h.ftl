<#include "FileHeader.inc.ftl">
<@file_header generatorDescription/>

<@include_guard_begin package.path, name/>

#include <array>
#include <zserio/Enums.h>
<@system_includes headerSystemIncludes, false/>

<@user_includes headerUserIncludes, true/>
<@namespace_begin package.path/>

enum class ${name} : ${baseCppTypeName}
{
<#list items as item>
    ${item.name} = ${item.value}<#if item?has_next>,</#if>
</#list>
};

<@namespace_end package.path/>

// This is full specialization for ${name} enumeration.
<@namespace_begin ["zserio"]/>

template<>
struct EnumTraits<${fullName}>
{
    static constexpr std::array<const char*, ${items?size}> names =
    {{
<#list items as item>
        "${item.name}"<#if item?has_next>,</#if>
</#list>
    }};

    static constexpr std::array<${fullName}, ${items?size}> values =
    {{
<#list items as item>
        ${item.fullName}<#if item?has_next>,</#if>
</#list>
    }};
};

template<>
size_t enumToOrdinal<${fullName}>(${fullName} value);

template<>
${fullName} valueToEnum<${fullName}>(
        typename std::underlying_type<${fullName}>::type rawValue);

template <>
inline constexpr size_t bitSizeOf<${fullName}>()
{
    return ${bitSize};
}

template <>
inline constexpr size_t initializeOffsets<${fullName}>(size_t bitPosition)
{
    return bitPosition + ${bitSize};
}

template <>
inline ${fullName} read<${fullName}>(zserio::BitStreamReader& in)
{
    return valueToEnum<${fullName}>(
            static_cast<typename std::underlying_type<${fullName}>::type>(
                    in.read${runtimeFunction.suffix}(${runtimeFunction.arg!})));
}

template <>
inline void write<${fullName}>(BitStreamWriter& out, ${fullName} value)
{
    out.write${runtimeFunction.suffix}(enumToValue(value)<#rt>
            <#lt><#if runtimeFunction.arg??>, ${runtimeFunction.arg}</#if>);
}

<@namespace_end ["zserio"]/>

<@include_guard_end package.path, name/>
