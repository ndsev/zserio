<#include "FileHeader.inc.ftl">
<@file_header generatorDescription/>

#include <string>

#include <zserio/HashCodeUtil.h>
<@system_includes cppSystemIncludes/>

<@user_include package.path, "${name}.h"/>
<@user_includes cppUserIncludes, true/>
<@namespace_begin package.path/>
<#if values?has_content>

<#list values as value>
const ${name} ${name}::Values::${value.name} = ${name}(${value.value});
</#list>
</#if>

${name}::${name}() noexcept :
    m_value(0)
{
}

${name}::${name}(zserio::BitStreamReader& in) :
    m_value(readValue(in))
{
}

${name}::${name}(underlying_type value) noexcept :
    m_value(value)
{
}

${name}::underlying_type ${name}::getValue() const
{
    return m_value;
}

size_t ${name}::bitSizeOf(size_t) const
{
<#if runtimeFunction.arg??>
    return ${runtimeFunction.arg};
<#else>
    return ::zserio::bitSizeOf${runtimeFunction.suffix}(::zserio::enumToValue(value));
</#if>
}
<#if withWriterCode>

size_t ${name}::initializeOffsets(size_t bitPosition) const
{
    return bitPosition + bitSizeOf(bitPosition);
}
</#if>

bool ${name}::operator==(const ${name}& other) const
{
    return m_value == other.m_value;
}

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
    out.write${runtimeFunction.suffix}(m_value<#if runtimeFunction.arg??>, ${runtimeFunction.arg}</#if>);
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
    return static_cast<underlying_type>(in.read${runtimeFunction.suffix}(${runtimeFunction.arg!}));
}
<@namespace_end package.path/>
