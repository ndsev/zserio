<#include "FileHeader.inc.ftl">
<@file_header generatorDescription/>

<@include_guard_begin package.path, name/>

#include <array>

#include <zserio/Enums.h>
<#if !bitSize??>
#include <zserio/BitSizeOfCalculator.h>
</#if>
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

template <>
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

template <>
size_t enumToOrdinal<${fullName}>(${fullName} value);

template <>
${fullName} valueToEnum<${fullName}>(
        typename std::underlying_type<${fullName}>::type rawValue);

template <>
size_t bitSizeOf<${fullName}>(${fullName}<#if !runtimeFunction.arg??> value</#if>);

template <>
size_t initializeOffsets<${fullName}>(size_t bitPosition, ${fullName} value);

template <>
${fullName} read<${fullName}>(zserio::BitStreamReader& in);

template <>
void write<${fullName}>(BitStreamWriter& out, ${fullName} value);

<@namespace_end ["zserio"]/>

<@include_guard_end package.path, name/>
