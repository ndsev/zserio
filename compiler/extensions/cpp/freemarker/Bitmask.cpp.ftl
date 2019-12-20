<#include "FileHeader.inc.ftl">
<@file_header generatorDescription/>

#include <string>

#include <zserio/HashCodeUtil.h>
<@system_includes cppSystemIncludes/>

<@user_include package.path, "${name}.h"/>
<@user_includes cppUserIncludes, true/>
<@namespace_begin package.path/>

${name}::${name}(zserio::BitStreamReader& in) :
    m_value(readValue(in))
{
}

${name}::operator underlying_type() const
{
    return m_value;
}

${name}::underlying_type ${name}::getValue() const
{
    return m_value;
}

size_t ${name}::bitSizeOf(size_t) const
{
    return ${runtimeFunction.arg};
}
<#if withWriterCode>

size_t ${name}::initializeOffsets(size_t bitPosition) const
{
    return bitPosition + bitSizeOf(bitPosition);
}
</#if>

int ${name}::hashCode() const
{
    int result = ::zserio::HASH_SEED;
    result = ::zserio::calcHashCode(result, m_value);
    return result;
}

void ${name}::read(::zserio::BitStreamReader& in)
{
    m_value = readValue(in);
}
<#if withWriterCode>

void ${name}::write(::zserio::BitStreamWriter& out, ::zserio::PreWriteAction) const
{
    out.write${runtimeFunction.suffix}(m_value, ${runtimeFunction.arg});
}
</#if>

std::string ${name}::toString() const
{
    std::string result;
<#list values as value>
    if ((*this & ${name}::Values::${value.name}) == ${name}::Values::${value.name})
    <#if value?is_first>
        result += "${value.name}";
    <#else>
        result += result.empty() ? "${value.name}" : " | ${value.name}"; 
    </#if>
</#list>
    return result.empty() ? "0" : result;
}

${name}::underlying_type ${name}::readValue(::zserio::BitStreamReader& in)
{
    return static_cast<underlying_type>(in.read${runtimeFunction.suffix}(${runtimeFunction.arg}));
}
<@namespace_end package.path/>
