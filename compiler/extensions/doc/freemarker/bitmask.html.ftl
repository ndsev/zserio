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
${I}  <del><i>Bitmask</i> ${symbol.name}</del>
<#else>
${I}  <i>Bitmask</i> ${symbol.name}
</#if>
${I}</h2>
    <@doc_comments docComments, indent, false/>

    <@code_table_begin indent/>
${I}  <tr><td colspan=3>bitmask <@symbol_reference typeSymbol/> ${symbol.name}</td></tr>
${I}  <tr><td>{</td><td rowspan="${values?size+1}">&nbsp;</td><td></td></tr>
<#list values as value>
${I}  <tr>
${I}    <td class="indent"><@symbol_reference value.symbol/></td>
${I}    <td>= ${value.value}<#if value_has_next>,</#if></td>
${I}  </tr>
</#list>
${I}  <tr><td colspan=3>};</td></tr>
    <@code_table_end indent/>

${I}<h3>Value Details</h3>

${I}<dl>
<#list values as value>
${I}  <dt class="memberItem"><a class="anchor" id="${value.symbol.htmlLink.htmlAnchor}">${value.symbol.name}:</a></dt>
${I}  <dd class="memberDetail">
        <@doc_comments value.docComments, indent+2/>
    <#list value.seeSymbols as seeSymbol>
${I}    <div class="doc"><span>see: </span>case <@symbol_reference seeSymbol.memberSymbol/> <#rt>
          <#lt>(<@symbol_reference seeSymbol.typeSymbol/>)</div>
    </#list>
${I}  </dd>
</#list>
${I}</dl>
    <@used_by usedByList, indent/>
<#if collaborationDiagramSvgUrl??>

    <@collaboration_diagram collaborationDiagramSvgUrl, indent/>
</#if>
