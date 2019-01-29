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
<#list parameters as parameter>
    virtual <@inspector_parameter_provider_return_type parameter/> <@inspector_parameter_provider_getter_name parameter/>();
</#list>
<#if parameters?has_content && explicitParameters?has_content>

</#if>
<#list explicitParameters as parameter>
    virtual <@inspector_parameter_provider_return_type parameter/> <@inspector_parameter_provider_getter_name parameter/>();
</#list>
};

<@namespace_end rootPackage.path/>

<@include_guard_end rootPackage.path, "InspectorParameterProvider"/>
