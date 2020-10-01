<#ftl output_format="HTML">
<#macro symbol_reference symbol>
    <#if symbol.htmlLink??>
<a class="${symbol.htmlClass}" title="${symbol.htmlTitle}" <#t>
  href = "${symbol.htmlLink.htmlPage}#${symbol.htmlLink.htmlAnchor}" target="detailedDocu">${symbol.name}</a><#t>
    <#else>
<span class="${symbol.htmlClass}" title="${symbol.htmlTitle}">${symbol.name}</span><#t>
    </#if>
</#macro>
