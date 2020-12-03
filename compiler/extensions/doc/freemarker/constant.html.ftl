<#ftl output_format="HTML">
<#include "doc_comment.inc.ftl">
<#include "code.inc.ftl">
<#include "symbol.inc.ftl">
<#include "usedby.inc.ftl">
<#include "svg_diagram.inc.ftl">
<#assign indent = 5>
<#assign I>${""?left_pad(indent * 2)}</#assign>

<#if hasFloatingDocComments(docComments)>
    <@doc_comments_floating docComments, indent/>

</#if>
${I}<h2 class="anchor" id="${symbol.htmlLink.htmlAnchor}">
${I}  <span<#if docComments.isDeprecated> class="deprecated"</#if>>Constant ${symbol.name}</span>
${I}</h2>

    <@code_table_begin indent/>
${I}  <tbody>
<#if hasStickyDocComments(docComments)>
${I}    <tr class="doc"><td>
          <@doc_comments_sticky docComments, indent+3/>
${I}    </td></tr>
</#if>
${I}    <tr><td>
${I}      const <@symbol_reference typeSymbol/> <@symbol_reference symbol/> = ${value};
${I}    </td></tr>
${I}  <tbody>
    <@code_table_end indent/>
    <@used_by symbol, usedBySymbols, indent/>
<#if collaborationDiagramSvg??>

    <@collaboration_diagram symbol, collaborationDiagramSvg, indent/>
</#if>
