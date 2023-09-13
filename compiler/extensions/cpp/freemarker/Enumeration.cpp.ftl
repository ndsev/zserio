<#include "FileHeader.inc.ftl">
<#include "TypeInfo.inc.ftl">
<@file_header generatorDescription/>

#include <zserio/StringConvertUtil.h>
#include <zserio/CppRuntimeException.h>
<#if withTypeInfoCode>
#include <zserio/TypeInfo.h>
    <#if withReflectionCode>
<@type_includes types.anyHolder/>
<@type_includes types.reflectableFactory/>
    </#if>
</#if>
<@system_includes cppSystemIncludes/>

<@user_include package.path, "${name}.h"/>
<@user_includes cppUserIncludes, false/>
<@namespace_begin ["zserio"]/>

<#macro enum_array_traits_type_name arrayTraits fullName bitSize>
    ${arrayTraits.name}<#t>
    <#if arrayTraits.isTemplated>
            <typename ::std::underlying_type<${fullName}>::type<#t>
            <#if arrayTraits.requiresElementFixedBitSize>, ${bitSize.value}</#if><#t>
            <#if arrayTraits.requiresElementDynamicBitSize>, EnumTraits<${fullName}>::ZserioElementBitSize</#if>><#t>
    </#if>
</#macro>
// This is full specialization of enumeration traits and methods for ${name} enumeration.
constexpr ::std::array<const char*, ${items?size}> EnumTraits<${fullName}>::names;
constexpr ::std::array<${fullName}, ${items?size}> EnumTraits<${fullName}>::values;
constexpr const char* EnumTraits<${fullName}>::enumName;
<#if underlyingTypeInfo.arrayTraits.isTemplated && underlyingTypeInfo.arrayTraits.requiresElementDynamicBitSize>

uint8_t EnumTraits<${fullName}>::ZserioElementBitSize::get()
{
    return ${bitSize.value};
}
</#if>
<#if withTypeInfoCode>

template <>
const ${types.typeInfo.name}& enumTypeInfo<${fullName}, ${types.allocator.default}>()
{
    using allocator_type = ${types.allocator.default};

    <@underlying_type_info_type_arguments_var "underlyingTypeArguments", bitSize!/>

    <@item_info_array_var "items", items/>

    static const ::zserio::EnumTypeInfo<allocator_type> typeInfo = {
        ::zserio::makeStringView("${schemaTypeName}"),
        <@type_info underlyingTypeInfo/>, underlyingTypeArguments, items
    };

    return typeInfo;
}
    <#if withReflectionCode>

template <>
${types.reflectablePtr.name} enumReflectable(${fullName} value, const ${types.allocator.default}& allocator)
{
    class Reflectable : public ::zserio::ReflectableBase<${types.allocator.default}>
    {
    public:
        explicit Reflectable(${fullName} value) :
                ::zserio::ReflectableBase<${types.allocator.default}>(
                        ::zserio::enumTypeInfo<${fullName}, ${types.allocator.default}>()),
                m_value(value)
        {}

        size_t bitSizeOf(size_t) const override
        {
            return ::zserio::bitSizeOf(m_value);
        }

        void write(::zserio::BitStreamWriter&<#if withWriterCode> writer</#if>) const override
        {
            <#if withWriterCode>
            ::zserio::write(writer, m_value);
            <#else>
            throw ::zserio::CppRuntimeException("Reflectable '${name}': ") <<
                    "Writer code is disabled by -withoutWriterCode zserio option!";
            </#if>
        }

        ${types.anyHolder.name} getAnyValue(const ${types.allocator.default}& allocator) const override
        {
            return ${types.anyHolder.name}(m_value, allocator);
        }

        ${types.anyHolder.name} getAnyValue(const ${types.allocator.default}& allocator) override
        {
            return ${types.anyHolder.name}(m_value, allocator);
        }

        ${underlyingTypeInfo.typeFullName} get<#rt>
                <#lt><#if !underlyingTypeInfo.isSigned>U</#if>Int${underlyingTypeInfo.typeNumBits}() const override
        {
            return static_cast<typename ::std::underlying_type<${fullName}>::type>(m_value);
        }

        <#if underlyingTypeInfo.isSigned>int64_t toInt()<#else>uint64_t toUInt()</#if> const override
        {
            return static_cast<typename ::std::underlying_type<${fullName}>::type>(m_value);
        }

        double toDouble() const override
        {
            return static_cast<double>(<#if underlyingTypeInfo.isSigned>toInt()<#else>toUInt()</#if>);
        }

        ${types.string.name} toString(const ${types.allocator.default}& allocator) const override
        {
            return ${types.string.name}(::zserio::enumToString(m_value), allocator);
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
        throw ::zserio::CppRuntimeException("Unknown value for enumeration ${name}: ") <<
                static_cast<typename ::std::underlying_type<${fullName}>::type>(value) << "!";
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
        return static_cast<${fullName}>(rawValue);
    default:
        throw ::zserio::CppRuntimeException("Unknown value for enumeration ${name}: ") << rawValue << "!";
    }
}

template <>
uint32_t enumHashCode<${fullName}>(${fullName} value)
{
    uint32_t result = ::zserio::HASH_SEED;
    result = ::zserio::calcHashCode(result, enumToValue(value));
    return result;
}

template <>
void initPackingContext(::zserio::DeltaContext& context, ${fullName} value)
{
    context.init<<@enum_array_traits_type_name underlyingTypeInfo.arrayTraits, fullName, bitSize!/>>(
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
size_t bitSizeOf(::zserio::DeltaContext& context, ${fullName} value)
{
    return context.bitSizeOf<<@enum_array_traits_type_name underlyingTypeInfo.arrayTraits, fullName, bitSize!/>>(
            ::zserio::enumToValue(value));
}
<#if withWriterCode>

template <>
size_t initializeOffsets(size_t bitPosition, ${fullName} value)
{
    return bitPosition + bitSizeOf(value);
}

template <>
size_t initializeOffsets(::zserio::DeltaContext& context, size_t bitPosition, ${fullName} value)
{
    return bitPosition + bitSizeOf(context, value);
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
${fullName} read(::zserio::DeltaContext& context, ::zserio::BitStreamReader& in)
{
    return valueToEnum<${fullName}>(context.read<<@enum_array_traits_type_name underlyingTypeInfo.arrayTraits, fullName, bitSize!/>>(
            in));
}
<#if withWriterCode>

<#function has_removed_items items>
    <#list items as item>
        <#if item.isRemoved>
            <#return true>
        </#if>
    </#list>
    <#return false>
</#function>
<#macro removed_items_check items>
    <#local isFirst=true/>
    if (<#rt>
    <#list items as item>
        <#if item.isRemoved>
            <#if isFirst>
                <#local isFirst=false/>
            value == ${item.fullName}<#t>
            <#else>
                <#lt> ||
            value == ${item.fullName}<#rt>
            </#if>
        </#if>
    </#list>
    <#lt>)
    {
        throw ::zserio::CppRuntimeException("Trying to write removed enumeration item '") <<
                ::zserio::enumToString(value) << "'!";
    }
</#macro>
template <>
void write(::zserio::BitStreamWriter& out, ${fullName} value)
{
    <#if has_removed_items(items)>
    <@removed_items_check items/>
    </#if>
    out.write${runtimeFunction.suffix}(::zserio::enumToValue(value)<#rt>
            <#lt><#if runtimeFunction.arg??>, ${runtimeFunction.arg}</#if>);
}

template <>
void write(::zserio::DeltaContext& context, ::zserio::BitStreamWriter& out, ${fullName} value)
{
    <#if has_removed_items(items)>
    <@removed_items_check items/>
    </#if>
    context.write<<@enum_array_traits_type_name underlyingTypeInfo.arrayTraits, fullName, bitSize!/>>(
            out, ::zserio::enumToValue(value));
}
</#if>
<@namespace_end ["zserio"]/>
