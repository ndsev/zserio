<#include "FileHeader.inc.ftl">
<@file_header generatorDescription/>

#include "<@include_path package.path, "${name}.h"/>"

<@namespace_begin package.path/>

${name}::${name}()
{
}

${name}::~${name}()
{
}

<#list fields as field>
<#if !field.isSimpleType>
${field.cppTypeName}& ${name}::get${field.name?cap_first}()
{
    return m_${field.name}.get();
}

</#if>
${field.cppArgumentTypeName} ${name}::get${field.name?cap_first}() const
{
    return m_${field.name}.get();
}

void ${name}::set${field.name?cap_first}(${field.cppArgumentTypeName} ${field.name})
{
    m_${field.name}.set(${field.name});
}

bool ${name}::isNull${field.name?cap_first}() const
{
    return !m_${field.name}.isSet();
}

void ${name}::setNull${field.name?cap_first}()
{
    m_${field.name}.reset();
}

</#list>
<@namespace_end package.path/>
