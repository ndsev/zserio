<#include "FileHeader.inc.ftl">
<@file_header generatorDescription/>

<@pybind_includes/>
<#if packageIncludes?has_content>

    <#list packageIncludes as packageInclude>
#include <${packageInclude}>
    </#list>
</#if>

PYBIND11_MODULE(zserio_gen_${rootPackageName}, m)
{
<#list packagePrefixes as packagePrefix>
    ${packagePrefix}zserioInitPyBind11(m);
</#list>
}
