<#include "FileHeader.inc.ftl">
<#include "DocComment.inc.ftl">
<@file_header generatorDescription/>

<@include_guard_begin package.path, name/>

<@runtime_version_check generatorVersion/>

<@system_includes headerSystemIncludes/>
<@user_includes headerUserIncludes/>
<@namespace_begin package.path/>

<#if withCodeComments && docComments??>
<@doc_comments docComments/>
</#if>
constexpr ${typeInfo.typeFullName} ${name} = <#if typeInfo.isSimple>static_cast<${typeInfo.typeFullName}>(</#if><#rt>
        <#lt>${value}<#if typeInfo.isSimple>)</#if>;
<@namespace_end package.path/>

<@include_guard_end package.path, name/>
