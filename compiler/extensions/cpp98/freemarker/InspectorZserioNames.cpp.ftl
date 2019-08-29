<#include "FileHeader.inc.ftl">
<#include "Inspector.inc.ftl">
<@file_header generatorDescription/>

#include "<@include_path rootPackage.path, "InspectorZserioNames.h"/>"

<@namespace_begin rootPackage.path/>

<#list zserioNames as zserioName>
const zserio::StringHolder InspectorZserioNames::<@inspector_zserio_name zserioName/>("${zserioName}");
</#list>

<@namespace_end rootPackage.path/>
