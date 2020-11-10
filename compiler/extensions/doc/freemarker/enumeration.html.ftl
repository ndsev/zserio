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
${I}  <del><i>Enumeration</i> ${symbol.name}</del>
<#else>
${I}  <i>Enumeration</i> ${symbol.name}
</#if>
${I}</h2>
    <@doc_comments docComments, indent, false/>

    <@code_table_begin indent/>
${I}  <tr><td colspan=3>enum <@symbol_reference typeSymbol/> ${symbol.name}</td></tr>
${I}  <tr><td>{</td><td rowspan="${items?size+1}">&nbsp;</td><td></td></tr>
<#list items as item>
${I}  <tr>
${I}    <td class="indent"><@symbol_reference item.symbol/></td>
${I}    <td>= ${item.value}<#if item_has_next>,</#if></td>
${I}  </tr>
</#list>
${I}  <tr><td colspan=3>};</td></tr>
    <@code_table_end indent/>

${I}<h3>Item Details</h3>

${I}<dl>
<#list items as item>
${I}  <dt class="memberItem"><a class="anchor" id="${item.symbol.htmlLink.htmlAnchor}">${item.symbol.name}:</a></dt>
${I}  <dd class="memberDetail">
        <@doc_comments item.docComments, indent+2/>
  <#list item.seeSymbols as seeSymbol>
${I}    <div class="doc"><span>see: </span>case <@symbol_reference seeSymbol.memberSymbol/> <#rt>
          <#lt>in choice <@symbol_reference seeSymbol.typeSymbol/></div>
  </#list>
${I}  </dd>
</#list>
${I}</dl>
    <@used_by usedBySymbols, indent/>
<#if collaborationDiagramSvg??>

    <@collaboration_diagram collaborationDiagramSvg, indent/>
</#if>
