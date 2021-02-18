<#include "FileHeader.inc.ftl">
<@file_header generatorDescription/>
<#if subpackages?has_content>

    <#list subpackages as subpackage>
import <#if packagePath?has_content>${packagePath}.</#if>${subpackage}.api as ${subpackage}
    </#list>
</#if>
<#if modules?has_content>

    <#list modules as module>
import <#if packagePath?has_content>${packagePath}.</#if>${module} as ${module}
    </#list>
</#if>
<#if packageSymbols?has_content>

    <#list packageSymbols as packageSymbol>
from <#if packagePath?has_content>${packagePath}.</#if>${packageSymbol} import ${packageSymbol}
    </#list>
</#if>
