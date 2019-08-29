<#include "FileHeader.inc.ftl">
<#include "Inspector.inc.ftl">
<@file_header generatorDescription/>

<@include_guard_begin rootPackage.path, "InspectorZserioNames"/>

#include <zserio/StringHolder.h>
<@system_includes headerSystemIncludes, false/>

<@namespace_begin rootPackage.path/>

/**
 * Contains all Zserio names neccessary for Blob Inspector Tree.
 */
class InspectorZserioNames
{
public:
<#list zserioNames as zserioName>
    static const zserio::StringHolder <@inspector_zserio_name zserioName/>;
</#list>
};

<@namespace_end rootPackage.path/>

<@include_guard_end rootPackage.path, "InspectorZserioNames"/>
