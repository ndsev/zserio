<#include "FileHeader.inc.ftl">
<@file_header generatorDescription/>

#include "zserio/CppRuntimeException.h"
#include "zserio/StringConvertUtil.h"

// This is full specialization for ${name} enumeration.
<@namespace_begin ["zserio"]/>

constexpr std::array<const char*, ${items?size}> EnumTraits<enumeration_types::bitfield_enum::Color>::names;
constexpr std::array<${fullName}, ${items?size}> EnumTraits<${fullName}>::values;

template<>
size_t enumToOrdinal<${fullName}>(${fullName} value)
{
    switch (value)
    {
<#list items as item>
    case ${item.fullName}:
        return ${item?index};
</#list>
    default:
        throw zserio::CppRuntimeException("Unknown value for enumeration ${name}: " +
                zserio::convertToString(static_cast<${baseCppTypeName}>(value)) + "!");
    }
}

template<>
${fullName} valueToEnum(
        typename std::underlying_type<${fullName}>::type rawValue)
{
    switch (rawValue)
    {
<#list items as item>
    case ${item.value}:
</#list>
        return ${fullName}(rawValue);
    default:
        throw zserio::CppRuntimeException("Unknown value for enumeration ${name}: " +
                zserio::convertToString(rawValue) + "!");
    }
}

<@namespace_end ["zserio"]/>
