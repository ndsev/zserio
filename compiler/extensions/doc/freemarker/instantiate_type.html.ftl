<#ftl output_format="HTML">
<#include "doc_comment.inc.ftl">
<#include "code.inc.ftl">
<#include "symbol.inc.ftl">
<#include "usedby.inc.ftl">
<#include "svg_diagram.inc.ftl">
<#assign indent = 5>
<#assign I>${""?left_pad(indent * 2)}</#assign>

${I}<h2 class="anchor" id="${symbol.htmlLink.htmlAnchor}">
${I}  <span<#if docComments.isDeprecated> class="deprecated"</#if>>Instantiate Type ${symbol.name}</span>
${I}</h2>
    <@doc_comments docComments, indent/>

    <@code_table_begin indent/>
${I}  <tbody>
${I}    <tr><td>
${I}      instantiate <@symbol_reference typeSymbol/> <@symbol_reference symbol/>;
${I}    </td></tr>
${I}  </tbody>
    <@code_table_end indent/>
    <@used_by symbol, usedBySymbols, indent/>
<#if collaborationDiagramSvg??>

    <@collaboration_diagram symbol, collaborationDiagramSvg, indent/>
</#if>
