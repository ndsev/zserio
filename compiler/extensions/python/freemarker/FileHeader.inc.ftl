<#macro file_header generatorDescription>
${generatorDescription}
</#macro>

<#macro package_imports packageImports>
    <#list packageImports as packageImport>
import ${packageImport}
    </#list>
</#macro>

<#macro type_imports typeImports>
    <#list typeImports as typeImport>
import ${typeImport}
    </#list>
</#macro>

<#macro symbol_imports symbolImports>
    <#list symbolImports as symbolImport>
import ${symbolImport}
    </#list>
</#macro>

<#macro all_imports packageImports typeImports symbolImports>
    <#if packageImports?has_content>

<@package_imports packageImports/>
    </#if>
    <#if symbolImports?has_content>

<@symbol_imports symbolImports/>
    </#if>
    <#if typeImports?has_content>

<@type_imports typeImports/>
    </#if>
</#macro>
