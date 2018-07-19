<#include "FileHeader.inc.ftl">
<#include "Inspector.inc.ftl">
<@file_header generatorDescription/>

#include "<@include_path rootPackage.path, "InspectorZserioTypeNames.h"/>"

<@namespace_begin rootPackage.path/>

<#list zserioTypeNames as zserioTypeName>
const zserio::StringHolder InspectorZserioTypeNames::<@inspector_zserio_type_name zserioTypeName/>("${zserioTypeName}");
</#list>

<@namespace_end rootPackage.path/>
