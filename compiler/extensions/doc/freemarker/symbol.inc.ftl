<#ftl output_format="HTML">
<#macro symbol_reference symbol>
    <#if symbol.htmlLink??>
<a class="${symbol.htmlClass}" <@symbol_reference_href_title symbol/> target="main_window">${symbol.name}</a><#t>
    <#else>
<span class="${symbol.htmlClass}" <@symbol_reference_href_title symbol/>>${symbol.name}</span><#t>
    </#if>
</#macro>

<#macro symbol_reference_href_title symbol>
    <#if symbol.htmlLink??>
        href="${symbol.htmlLink.htmlPage}#${symbol.htmlLink.htmlAnchor}" title="${symbol.htmlTitle}"<#t>
    <#else>
        title="${symbol.htmlTitle}"<#t>
    </#if>
</#macro>

<#macro symbol_reference_url symbol>
    <#if symbol.htmlLink??>
        URL="${symbol.htmlLink.htmlPage}#${symbol.htmlLink.htmlAnchor}"<#t>
    </#if>
</#macro>

<#macro symbol_reference_tooltip symbol>
    tooltip="${symbol.htmlTitle}"<#t>
</#macro>
