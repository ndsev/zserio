<#include "FileHeader.inc.ftl">
<#include "DocComment.inc.ftl">
<@file_header generatorDescription/>

<@include_guard_begin package.path, name/>

#include <pybind11/pybind11.h>
<@system_includes headerSystemIncludes/>
<@user_includes headerUserIncludes/>
<@namespace_begin package.path/>

<#if withCodeComments && docComments??>
<@doc_comments docComments/>
</#if>
const ${typeInfo.typeFullName} ${name} = ${value};

inline void zserioInitPyBind11_${name}(::pybind11::module_ m)
{
    m.attr("${name}") = ::pybind11::cast(<#if typeInfo.isString>::zserio::toString(${value})<#else>${value}</#if>);
}
<@namespace_end package.path/>

<@include_guard_end package.path, name/>
