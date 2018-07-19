<#include "FileHeader.inc.ftl">
<@file_header generatorDescription/>

<@include_guard_begin rootPackage.path, "IParameterProvider"/>

<#if sqlTableParameters?has_content>
#include <sqlite3.h>
</#if>
<@system_includes headerSystemIncludes, true/>
<@user_includes headerUserIncludes, true/>
<@namespace_begin rootPackage.path/>

class IParameterProvider
{
public:
<#if sqlTableParameters?has_content>
    <#list sqlTableParameters as sqlTableParameter>
        <#if sqlTableParameter.isExplicit>
    virtual ${sqlTableParameter.cppTypeName} get${sqlTableParameter.tableName}_${sqlTableParameter.expression}(sqlite3_stmt& statement) = 0;
        </#if>
    </#list>

</#if>
    virtual ~IParameterProvider()
    {}
};

<@namespace_end rootPackage.path/>

<@include_guard_end rootPackage.path, "IParameterProvider"/>
