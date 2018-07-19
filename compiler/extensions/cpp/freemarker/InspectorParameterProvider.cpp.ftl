<#include "FileHeader.inc.ftl">
<#include "Inspector.inc.ftl">
<@file_header generatorDescription/>

#include <zserio/CppRuntimeException.h>

#include "<@include_path rootPackage.path, "InspectorParameterProvider.h"/>"

<@namespace_begin rootPackage.path/>

<#list sqlTableParameters as parameter>
${parameter.cppTypeName} InspectorParameterProvider::<@inspector_parameter_provider_name parameter/>()
{
    throw zserio::CppRuntimeException("InspectorParameterProvider: "
            "'<@inspector_parameter_provider_name parameter/>' not implemented yet!");
}

</#list>
<@namespace_end rootPackage.path/>
