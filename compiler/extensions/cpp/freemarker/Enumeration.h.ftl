<#include "FileHeader.inc.ftl">
<#include "DocComment.inc.ftl">
<@file_header generatorDescription/>

<@include_guard_begin package.path, name/>

#include <array>

#include <zserio/Enums.h>
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
<@type_includes types.packingContextNode/>
<@system_includes headerSystemIncludes/>
<@user_includes headerUserIncludes/>
<@namespace_begin package.path/>

<#if withCodeComments && docComments??>
<@doc_comments docComments/>
</#if>
enum class ${name} : ${underlyingTypeInfo.typeFullName}
{
<#list items as item>
    <#if withCodeComments && item.docComments??>
    <@doc_comments item.docComments, 1/>
    </#if>
    ${item.name} = ${item.value}<#if item?has_next>,</#if>
</#list>
};
<@namespace_end package.path/>
<@namespace_begin ["zserio"]/>

// This is full specialization of enumeration traits and methods for ${name} enumeration.
template <>
struct EnumTraits<${fullName}>
{
    static constexpr ::std::array<const char*, ${items?size}> names =
    {{
<#list items as item>
        "${item.name}"<#if item?has_next>,</#if>
</#list>
    }};

    static constexpr ::std::array<${fullName}, ${items?size}> values =
    {{
<#list items as item>
        ${item.fullName}<#if item?has_next>,</#if>
</#list>
    }};

    static constexpr const char* enumName = "${name}";
<#if underlyingTypeInfo.arrayTraits.isTemplated && underlyingTypeInfo.arrayTraits.requiresElementDynamicBitSize>

    class ZserioElementBitSize
    {
    public:
        static uint8_t get();
    };
</#if>
};
<#if withTypeInfoCode>

template <>
const ${types.typeInfo.name}& enumTypeInfo<${fullName}, ${types.allocator.default}>();
    <#if withReflectionCode>

template <>
${types.reflectablePtr.name} enumReflectable(${fullName} value, const ${types.allocator.default}& allocator);
    </#if>
</#if>

template <>
size_t enumToOrdinal<${fullName}>(${fullName} value);

template <>
${fullName} valueToEnum<${fullName}>(
        typename ::std::underlying_type<${fullName}>::type rawValue);

template <>
uint32_t enumHashCode<${fullName}>(${fullName} value);

template <>
void initPackingContext<${types.packingContextNode.name}, ${fullName}>(
        ${types.packingContextNode.name}& contextNode, ${fullName} value);

template <>
size_t bitSizeOf<${fullName}>(${fullName} value);

template <>
size_t bitSizeOf<${types.packingContextNode.name}, ${fullName}>(
        ${types.packingContextNode.name}& contextNode, ${fullName} value);
<#if withWriterCode>

template <>
size_t initializeOffsets<${fullName}>(size_t bitPosition, ${fullName} value);

template <>
size_t initializeOffsets<${types.packingContextNode.name}, ${fullName}>(
        ${types.packingContextNode.name}& contextNode, size_t bitPosition, ${fullName} value);
</#if>

template <>
${fullName} read<${fullName}>(::zserio::BitStreamReader& in);

template <>
${fullName} read<${fullName}, ${types.packingContextNode.name}>(
        ${types.packingContextNode.name}& contextNode, ::zserio::BitStreamReader& in);
<#if withWriterCode>

template <>
void write<${fullName}>(::zserio::BitStreamWriter& out, ${fullName} value);

template <>
void write<${types.packingContextNode.name}, ${fullName}>(
        ${types.packingContextNode.name}& contextNode, ::zserio::BitStreamWriter& out, ${fullName} value);
</#if>
<@namespace_end ["zserio"]/>

<@include_guard_end package.path, name/>
