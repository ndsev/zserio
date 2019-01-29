<#include "FileHeader.inc.ftl">
<@file_header generatorDescription/>

<@include_guard_begin package.path, name/>

<@system_includes headerSystemIncludes, false/>
#include <zserio/OptionalHolder.h>

<@user_includes headerUserIncludes, true/>
<@namespace_begin package.path/>

class ${name}
{
public:
    ${name}();
    ~${name}();

<#list fields as field>
    <#if !field.isSimpleType>
    ${field.cppTypeName}& get${field.name?cap_first}();
    </#if>
    ${field.cppArgumentTypeName} get${field.name?cap_first}() const;
    void set${field.name?cap_first}(${field.cppArgumentTypeName} ${field.name});
    bool isNull${field.name?cap_first}() const;
    void setNull${field.name?cap_first}();

</#list>
private:
<#list fields as field>
    zserio::InPlaceOptionalHolder<${field.cppTypeName}> m_${field.name};
</#list>
};

<@namespace_end package.path/>

<@include_guard_end package.path, name/>
