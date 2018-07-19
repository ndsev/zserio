<#include "FileHeader.inc.ftl">
<#include "Inspector.inc.ftl">
<@file_header generatorDescription/>

<@include_guard_begin rootPackage.path, "InspectorParameterProvider"/>

<@system_includes headerSystemIncludes, false/>

#include "<@include_path rootPackage.path, "IInspectorParameterProvider.h"/>"
<@user_includes headerUserIncludes, false/>

<@namespace_begin rootPackage.path/>

/**
 * Default implementation of IInspectorParameterProvider which just throws exceptions.
 */
class InspectorParameterProvider : public IInspectorParameterProvider
{
public:
<#list sqlTableParameters as parameter>
    virtual ${parameter.cppTypeName} <@inspector_parameter_provider_name parameter/>();
</#list>
};

<@namespace_end rootPackage.path/>

<@include_guard_end rootPackage.path, "InspectorParameterProvider"/>
