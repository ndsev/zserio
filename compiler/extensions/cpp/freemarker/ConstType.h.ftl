<#include "FileHeader.inc.ftl">
<#include "InstantiateTemplate.inc.ftl">
<@file_header generatorDescription/>

<@include_guard_begin rootPackage.path, "ConstType"/>

<@system_includes headerSystemIncludes, true/>
<@user_includes headerUserIncludes, true/>
<@namespace_begin rootPackage.path/>

namespace ConstType
{
<#list items as item>
    const ${item.cppTypeName} ${item.name} = ${item.value};
</#list>
} // namespace ConstType

<@namespace_end rootPackage.path/>

<@include_guard_end rootPackage.path, "ConstType"/>
