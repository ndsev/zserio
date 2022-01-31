<#include "FileHeader.inc.ftl">
<#include "TypeInfo.inc.ftl">
<@file_header generatorDescription/>

#include <zserio/StringConvertUtil.h>
#include <zserio/CppRuntimeException.h>
<#if withTypeInfoCode>
#include <zserio/TypeInfo.h>
    <#if withReflectionCode>
<@type_includes types.reflectableFactory/>
    </#if>
</#if>
<@system_includes cppSystemIncludes/>

<@user_include package.path, "${name}.h"/>
<@user_includes cppUserIncludes, false/>
<@namespace_begin ["zserio"]/>

<#macro enum_array_traits arrayTraits fullName bitSize>
    ${arrayTraits.name}<#t>
    <#if arrayTraits.isTemplated>
            <typename ::std::underlying_type<${fullName}>::type><#t>
    </#if>
    (<#if arrayTraits.requiresElementBitSize>${bitSize.value}</#if>)<#t>
</#macro>
// This is full specialization of enumeration traits and methods for ${name} enumeration.
constexpr ::std::array<const char*, ${items?size}> EnumTraits<${fullName}>::names;
constexpr ::std::array<${fullName}, ${items?size}> EnumTraits<${fullName}>::values;
<#if withTypeInfoCode>

template <>
const ITypeInfo& enumTypeInfo<${fullName}>()
{
    <@underlying_type_info_type_arguments_var "underlyingTypeArguments", bitSize!/>

    <@item_info_array_var "items", items/>

    static const ::zserio::EnumTypeInfo typeInfo = {
        ::zserio::makeStringView("${schemaTypeName}"),
        <@type_info underlyingTypeInfo/>, underlyingTypeArguments, items
    };

    return typeInfo;
}
    <#if withReflectionCode>

template <>
${types.reflectablePtr.name} enumReflectable(
        ${fullName} value, const ${types.allocator.default}& allocator)
{
    class Reflectable : public ::zserio::ReflectableBase<${types.allocator.default}>
    {
    public:
        explicit Reflectable(${fullName} value) :
                ::zserio::ReflectableBase<${types.allocator.default}>(::zserio::enumTypeInfo<${fullName}>()),
                m_value(value)
        {}

        virtual ${underlyingTypeInfo.typeFullName} get<#rt>
                <#lt><#if !underlyingTypeInfo.isSigned>U</#if>Int${underlyingTypeInfo.typeNumBits}() const override
        {
            return static_cast<typename ::std::underlying_type<${fullName}>::type>(m_value);
        }

        virtual <#if underlyingTypeInfo.isSigned>int64_t toInt()<#else>uint64_t toUInt()</#if> const override
        {
            return static_cast<typename ::std::underlying_type<${fullName}>::type>(m_value);
        }

        virtual double toDouble() const override
        {
            return static_cast<double>(<#if underlyingTypeInfo.isSigned>toInt()<#else>toUInt()</#if>);
        }

        virtual ${types.string.name} toString(
                const ${types.allocator.default}& allocator = ${types.allocator.default}()) const override
        {
            return ${types.string.name}(::zserio::enumToString(m_value), allocator);
        }

        virtual void write(::zserio::BitStreamWriter& writer) override
        {
            ::zserio::write(writer, m_value);
        }

        virtual size_t bitSizeOf(size_t) const override
        {
            return ::zserio::bitSizeOf(m_value);
        }

    private:
        ${fullName} m_value;
    };

    return std::allocate_shared<Reflectable>(allocator, value);
}
    </#if>
</#if>

template <>
size_t enumToOrdinal(${fullName} value)
{
    switch (value)
    {
<#list items as item>
    case ${item.fullName}:
        return ${item?index};
</#list>
    default:
        throw ::zserio::CppRuntimeException("Unknown value for enumeration ${name}: ") +
                static_cast<typename ::std::underlying_type<${fullName}>::type>(value) + "!";
    }
}

template <>
${fullName} valueToEnum(
        typename ::std::underlying_type<${fullName}>::type rawValue)
{
    switch (rawValue)
    {
<#list items as item>
    case ${item.value}:
</#list>
        return ${fullName}(rawValue);
    default:
        throw ::zserio::CppRuntimeException("Unknown value for enumeration ${name}: ") + rawValue + "!";
    }
}

template <>
void initPackingContext(${types.packingContextNode.name}& contextNode, ${fullName} value)
{
    contextNode.getContext().init(<@enum_array_traits underlyingTypeInfo.arrayTraits, fullName, bitSize!/>,
            ::zserio::enumToValue(value));
}

template <>
size_t bitSizeOf(${fullName}<#if !runtimeFunction.arg??> value</#if>)
{
<#if runtimeFunction.arg??>
    return ${runtimeFunction.arg};
<#else>
    return ::zserio::bitSizeOf${runtimeFunction.suffix}(::zserio::enumToValue(value));
</#if>
}

template <>
size_t bitSizeOf(${types.packingContextNode.name}& contextNode, ${fullName} value)
{
    return contextNode.getContext().bitSizeOf(
            <@enum_array_traits underlyingTypeInfo.arrayTraits, fullName, bitSize!/>,
            ::zserio::enumToValue(value));
}
<#if withWriterCode>

template <>
size_t initializeOffsets(size_t bitPosition, ${fullName} value)
{
    return bitPosition + bitSizeOf(value);
}

template <>
size_t initializeOffsets(${types.packingContextNode.name}& contextNode,
        size_t bitPosition, ${fullName} value)
{
    return bitPosition + bitSizeOf(contextNode, value);
}
</#if>

template <>
${fullName} read(::zserio::BitStreamReader& in)
{
    return valueToEnum<${fullName}>(
            static_cast<typename ::std::underlying_type<${fullName}>::type>(
                    in.read${runtimeFunction.suffix}(${runtimeFunction.arg!})));
}

template <>
${fullName} read(${types.packingContextNode.name}& contextNode, ::zserio::BitStreamReader& in)
{
    return valueToEnum<${fullName}>(contextNode.getContext().read(
            <@enum_array_traits underlyingTypeInfo.arrayTraits, fullName, bitSize!/>, in));
}
<#if withWriterCode>

template <>
void write(::zserio::BitStreamWriter& out, ${fullName} value)
{
    out.write${runtimeFunction.suffix}(::zserio::enumToValue(value)<#rt>
            <#lt><#if runtimeFunction.arg??>, ${runtimeFunction.arg}</#if>);
}

template <>
void write(${types.packingContextNode.name}& contextNode, ::zserio::BitStreamWriter& out, ${fullName} value)
{
    contextNode.getContext().write(
            <@enum_array_traits underlyingTypeInfo.arrayTraits, fullName, bitSize!/>,
            out, ::zserio::enumToValue(value));
}
</#if>
<@namespace_end ["zserio"]/>
