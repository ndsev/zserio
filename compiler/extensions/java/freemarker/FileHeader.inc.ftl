<#macro file_header generatorDescription>
${generatorDescription}
</#macro>

<#macro package packageName>
    <#if packageName?? && packageName != "">
package ${packageName};
    </#if>
</#macro>

<#macro standard_header generatorDescription packageName>
<@file_header generatorDescription/>

<@package packageName/>
</#macro>
