<#ftl output_format="HTML">
<#macro symbol_reference symbol indent = 2>
    <#local I>${""?left_pad(indent * 2)}</#local>
    <#if symbol.htmlLink??>
<#lt><a class="${symbol.htmlClass}" title="${symbol.htmlTitle}"
${I}href = "${symbol.htmlLink.htmlPage}#${symbol.htmlLink.htmlAnchor}" target="detailedDocu">${symbol.name}</a><#rt>
    <#else>
<#lt><span class="${symbol.htmlClass}" title="${symbol.htmlTitle}">${symbol.name}</span><#t>
    </#if>
</#macro>
