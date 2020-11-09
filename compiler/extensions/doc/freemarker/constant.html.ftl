<#ftl output_format="HTML">
<#include "doc_comment.inc.ftl">
<#include "code.inc.ftl">
<#include "symbol.inc.ftl">
<#include "usedby.inc.ftl">
<#include "svg_diagram.inc.ftl">
<#assign indent = 5>
<#assign I>${""?left_pad(indent * 2)}</#assign>

${I}<h2 class="anchor" id="${symbol.htmlLink.htmlAnchor}">
<#if docComments.isDeprecated>
${I}  <span class="deprecated">(deprecated) </span>
${I}  <del><i>Constant</i> ${symbol.name}</del>
<#else>
${I}  <i>Constant</i> ${symbol.name}
</#if>
${I}</h2>
    <@doc_comments docComments, indent, false/>

    <@code_table_begin indent/>
${I}  <tr><td>
${I}    const <@symbol_reference typeSymbol/> ${symbol.name} = ${value};
${I}  </td></tr>
    <@code_table_end indent/>
    <@used_by usedByList, indent/>
<#if collaborationDiagramSvg??>

    <@collaboration_diagram collaborationDiagramSvg, indent/>
</#if>
