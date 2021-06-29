<#include "FileHeader.inc.ftl">
<@file_header generatorDescription/>

#include <zserio/HashCodeUtil.h>
#include <zserio/StringConvertUtil.h>
<#if upperBound??>
#include <zserio/CppRuntimeException.h>
</#if>
<@system_includes cppSystemIncludes/>

<@user_include package.path, "${name}.h"/>
<@user_includes cppUserIncludes, false/>
<@namespace_begin package.path/>

${name}::${name}(::zserio::BitStreamReader& in) :
    m_value(readValue(in))
{
}
<#if upperBound??>

${name}::${name}(underlying_type value) :
    m_value(value)
{
    if (m_value > ${upperBound})
        throw ::zserio::CppRuntimeException("Value for bitmask '${name}' out of bounds: ") + value + "!";
}
</#if>

size_t ${name}::bitSizeOf(size_t) const
{
<#if runtimeFunction.arg??>
    return ${runtimeFunction.arg};
<#else>
    return ::zserio::bitSizeOf${runtimeFunction.suffix}(m_value);
</#if>
}
<#if withWriterCode>

size_t ${name}::initializeOffsets(size_t bitPosition) const
{
    return bitPosition + bitSizeOf(bitPosition);
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
<@namespace_end package.path/>
