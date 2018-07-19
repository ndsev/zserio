<#include "FileHeader.inc.ftl">
<#include "Inspector.inc.ftl">
<@file_header generatorDescription/>

<@include_guard_begin rootPackage.path, "IInspectorParameterProvider"/>

<@system_includes headerSystemIncludes, true/>
<@user_includes headerUserIncludes, true/>
<@namespace_begin rootPackage.path/>

/**
 * An interface for retrieving values of explicit sql_table parameters.
 */
class IInspectorParameterProvider
{
public:
<#if sqlTableParameters?has_content>
    <#list sqlTableParameters as parameter>
    virtual ${parameter.cppTypeName} <@inspector_parameter_provider_name parameter/>() = 0;
    </#list>

</#if>
    virtual ~IInspectorParameterProvider()
    {}
};

<@namespace_end rootPackage.path/>

<@include_guard_end rootPackage.path, "IInspectorParameterProvider"/>
