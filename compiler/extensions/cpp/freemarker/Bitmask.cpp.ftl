<#include "FileHeader.inc.ftl">
<#include "TypeInfo.inc.ftl">
<@file_header generatorDescription/>

#include <zserio/HashCodeUtil.h>
#include <zserio/StringConvertUtil.h>
<#if upperBound??>
#include <zserio/CppRuntimeException.h>
</#if>
<#if withTypeInfoCode>
#include <zserio/TypeInfo.h>
<@type_includes types.introspectableFactory/>
</#if>
<@system_includes cppSystemIncludes/>

<@user_include package.path, "${name}.h"/>
<@user_includes cppUserIncludes, false/>
<@namespace_begin package.path/>

<#macro bitmask_array_traits arrayTraits fullName bitSize>
    ${arrayTraits.name}<#t>
    <#if arrayTraits.isTemplated>
            <${fullName}::underlying_type><#t>
    </#if>
    (<#if arrayTraits.requiresElementBitSize>${bitSize.value}</#if>)<#t>
</#macro>
${name}::${name}(::zserio::BitStreamReader& in) :
        m_value(readValue(in))
{}

${name}::${name}(${types.packingContextNode.name}& contextNode, ::zserio::BitStreamReader& in) :
        m_value(readValue(contextNode, in))
{}
<#if upperBound??>

${name}::${name}(underlying_type value) :
        m_value(value)
{
    if (m_value > ${upperBound})
        throw ::zserio::CppRuntimeException("Value for bitmask '${name}' out of bounds: ") + value + "!";
}
</#if>
<#if withTypeInfoCode>

const ::zserio::ITypeInfo& ${name}::typeInfo()
{
    <@underlying_type_info_type_arguments_var "underlyingTypeArguments", bitSize!/>

    <@item_info_array_var "values", values/>

    static const ::zserio::BitmaskTypeInfo typeInfo = {
        ::zserio::makeStringView("${schemaTypeName}"),
        <@type_info underlyingTypeInfo/>, underlyingTypeArguments, values
    };

    return typeInfo;
}

${types.introspectablePtr.name} ${name}::introspectable(const ${types.allocator.default}& allocator)
{
    class Introspectable : public ::zserio::IntrospectableBase<${types.allocator.default}>
    {
    public:
        explicit Introspectable(${fullName} bitmask) :
                ::zserio::IntrospectableBase<${types.allocator.default}>(${fullName}::typeInfo()),
                m_bitmask(bitmask)
        {}

        <#-- bitmask is always unsigned -->
        virtual ${baseCppTypeName} getUInt${baseCppTypeNumBits}() const override
        {
            return m_bitmask.getValue();
        }

        virtual uint64_t toUInt() const override
        {
            return m_bitmask.getValue();
        }

        virtual double toDouble() const override
        {
            return toUInt();
        }

        virtual ${types.string.name} toString(
                const ${types.allocator.default}& allocator = ${types.allocator.default}()) const override
        {
            return m_bitmask.toString(allocator);
        }
    <#if withWriterCode>

        virtual void write(::zserio::BitStreamWriter& writer) override
        {
            m_bitmask.write(writer);
        }
    </#if>

    private:
        ${fullName} m_bitmask;
    };

    return ::std::allocate_shared<Introspectable>(allocator, *this);
}
</#if>

void ${name}::createPackingContext(${types.packingContextNode.name}& contextNode)
{
    contextNode.createContext();
}

void ${name}::initPackingContext(${types.packingContextNode.name}& contextNode) const
{
    contextNode.getContext().init(<@bitmask_array_traits arrayTraits, fullName, bitSize!/>,
            m_value);
}

size_t ${name}::bitSizeOf(size_t) const
{
<#if runtimeFunction.arg??>
    return ${runtimeFunction.arg};
<#else>
    return ::zserio::bitSizeOf${runtimeFunction.suffix}(m_value);
</#if>
}

size_t ${name}::bitSizeOf(${types.packingContextNode.name}& contextNode, size_t) const
{
    return contextNode.getContext().bitSizeOf(
            <@bitmask_array_traits arrayTraits, fullName, bitSize!/>,
            m_value);
}

<#if withWriterCode>

size_t ${name}::initializeOffsets(size_t bitPosition) const
{
    return bitPosition + bitSizeOf(bitPosition);
}

size_t ${name}::initializeOffsets(${types.packingContextNode.name}& contextNode, size_t bitPosition) const
{
    return bitPosition + bitSizeOf(contextNode, bitPosition);
}
</#if>

uint32_t ${name}::hashCode() const
{
    uint32_t result = ::zserio::HASH_SEED;
    result = ::zserio::calcHashCode(result, m_value);
    return result;
}
<#if withWriterCode>

void ${name}::write(::zserio::BitStreamWriter& out, ::zserio::PreWriteAction) const
{
    out.write${runtimeFunction.suffix}(m_value<#if runtimeFunction.arg??>, ${runtimeFunction.arg}</#if>);
}

void ${name}::write(${types.packingContextNode.name}& contextNode, ::zserio::BitStreamWriter& out) const
{
    contextNode.getContext().write(
            <@bitmask_array_traits arrayTraits, fullName, bitSize!/>,
            out, m_value);
}
</#if>

${types.string.name} ${name}::toString(const ${types.string.name}::allocator_type& allocator) const
{
    ${types.string.name} result(allocator);
<#list values as value>
    <#if !value.isZero>
    if ((*this & ${name}::Values::${value.name}) == ${name}::Values::${value.name})
        result += result.empty() ? "${value.name}" : " | ${value.name}";
    <#else>
        <#assign zeroValueName=value.name/><#-- may be there only once -->
    </#if>
</#list>
<#if zeroValueName??>
    if (result.empty() && m_value == 0)
        result += "${zeroValueName}";
</#if>

    return ::zserio::toString<${types.string.name}::allocator_type>(m_value, allocator) + "[" + result + "]";
}

${name}::underlying_type ${name}::readValue(::zserio::BitStreamReader& in)
{
    return static_cast<underlying_type>(in.read${runtimeFunction.suffix}(${runtimeFunction.arg!}));
}

${name}::underlying_type ${name}::readValue(${types.packingContextNode.name}& contextNode,
        ::zserio::BitStreamReader& in)
{
    return contextNode.getContext().read(
            <@bitmask_array_traits arrayTraits, fullName, bitSize!/>, in);
}
<@namespace_end package.path/>
