<#include "FileHeader.inc.ftl">
<#include "InstantiateTemplate.inc.ftl">
<#if withInspectorCode>
    <#include "Inspector.inc.ftl">
</#if>
<@file_header generatorDescription/>

#include <zserio/BitSizeOfCalculator.h>
#include <zserio/CppRuntimeException.h>
#include <zserio/StringConvertUtil.h>
<#if withInspectorCode>
#include <zserio/inspector/BlobInspectorTreeUtil.h>
</#if>
<@system_includes cppSystemIncludes, false/>

#include "<@include_path package.path, "${name}.h"/>"
<#if withInspectorCode>
#include "<@include_path rootPackage.path, "InspectorZserioTypeNames.h"/>"
#include "<@include_path rootPackage.path, "InspectorZserioNames.h"/>"
</#if>
<@user_includes cppUserIncludes, false/>

<@namespace_begin package.path/>

${name}::${name}() : m_value(static_cast<e_${name}>(0))
{
}

${name}::${name}(e_${name} value) : m_value(value)
{
}

${name}::${name}(zserio::BitStreamReader& _in)
{
    read(_in);
}

<#if withInspectorCode>

${name}::${name}(const zserio::BlobInspectorTree& _tree)
{
    read(_tree);
}
</#if>
${name}::operator e_${name}() const
{
    return m_value;
}

${baseCppTypeName} ${name}::getValue() const
{
    return m_value;
}

size_t ${name}::bitSizeOf(size_t) const
{
<#if bitSize??>
    return ${bitSize};
<#else>
    return zserio::getBitSizeOf${runtimeFunction.suffix}(m_value);
</#if>
}

size_t ${name}::initializeOffsets(size_t _bitPosition) const
{
    return _bitPosition + bitSizeOf(_bitPosition);
}

bool ${name}::operator==(const ${name}& other) const
{
    return m_value == other.m_value;
}

bool ${name}::operator==(e_${name} other) const
{
    return m_value == other;
}

int ${name}::hashCode() const
{
    return zserio::calcHashCode(zserio::HASH_SEED, static_cast<${baseCppTypeName}>(m_value));
}

void ${name}::read(zserio::BitStreamReader& _in)
{
    m_value = toEnum(_in.read${runtimeFunction.suffix}(${runtimeFunction.arg!}));
}
<#if withInspectorCode>

void ${name}::read(const zserio::BlobInspectorTree& _tree)
{
    const zserio::BlobInspectorNode _node = zserio::getBlobInspectorNode(_tree, 0,
            zserio::BlobInspectorNode::NT_VALUE);
    ${baseCppTypeName} _enumValue;
    std::string _enumSymbol;
    _node.getValue().get(_enumValue, _enumSymbol);
    const ${name} _readEnum(toEnum(_enumValue));
    if (_enumSymbol != _readEnum.toString())
    {
        throw zserio::CppRuntimeException("Read: Wrong enumeration symbol for enumeration ${name}: " +
                _enumSymbol + " != " + _readEnum.toString() + "!");
    }

    m_value = _readEnum;
}
</#if>

void ${name}::write(zserio::BitStreamWriter& _out, zserio::PreWriteAction) const
{
    _out.write${runtimeFunction.suffix}(<#rt>
            <#lt><@instantiate_template "static_cast", baseCppTypeName/>(m_value)<#rt>
            <#lt><#if runtimeFunction.arg??>, ${runtimeFunction.arg}</#if>);
}
<#if withInspectorCode>

void ${name}::write(zserio::BitStreamWriter& _out, zserio::BlobInspectorTree& _tree,
        zserio::PreWriteAction _preWriteAction) const
{
    zserio::BlobInspectorNode& _node = _tree.createChild(zserio::BlobInspectorNode::NT_VALUE,
            ${rootPackage.name}::InspectorZserioTypeNames::<@inspector_zserio_type_name zserioTypeName/>,
            ${rootPackage.name}::InspectorZserioNames::<@inspector_zserio_name name/>);
    const size_t _startBitPosition = _out.getBitPosition();
    _node.getValue().set(getValue(), toString());
    write(_out, _preWriteAction);
    _node.setZserioDescriptor(_startBitPosition, _out.getBitPosition());
}
</#if>

const char* ${name}::toString() const
{
    switch (m_value)
    {
<#list items as item>
    case ${item.name}:
        return "${item.name}";
</#list>
    default:
        return "UNKNOWN";
    }
}

${name} ${name}::toEnum(${baseCppTypeName} rawValue)
{
    switch (rawValue)
    {
<#list items as item>
    case ${item.value}:
        return ${item.name};

</#list>
    default:
        throw zserio::CppRuntimeException("Unknown value for enumeration ${name}: " +
                zserio::convertToString(rawValue) + "!");
    }
}

<@namespace_end package.path/>
