<#include "FileHeader.inc.ftl">
<@file_header generatorDescription/>

<@user_include package.path, "ZserioPyBind11.h"/>
<#list subpackages as subpackage>
<@user_include subpackage.path, "ZserioPyBind11.h"/>
</#list>
<@user_includes cppUserIncludes, false/>
<@namespace_begin package.path/>

void zserioInitPyBind11(::pybind11::module_ m)
{
    auto submodule = m.def_submodule("${package.path?last}");
<#if subpackages?has_content>

    <#list subpackages as subpackage>
    ${subpackage.name}::zserioInitPyBind11(submodule);
    </#list>
</#if>
<#if packageTypes?has_content>

    <#list packageTypes as typeInfo>
        <#if !typeInfo.isSimple && !typeInfo.isString>
    ${typeInfo.typeFullName}::zserioInitPyBind11(submodule);
        </#if>
    </#list>
</#if>
<#if packageSymbols?has_content>

    <#list packageSymbols as symbol>
    ${package.name}::zserioInitPyBind11_${symbol.name}(submodule);
    </#list>
</#if>
}
<@namespace_end package.path/>