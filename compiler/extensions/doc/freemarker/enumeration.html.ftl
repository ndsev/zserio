<#ftl output_format="HTML">
<#include "doc_comment.inc.ftl">
<#include "symbol.inc.ftl">
<#include "usedby.inc.ftl">
<#include "svg_diagram.inc.ftl">

    <h2 class="anchor" id="${symbol.htmlLink.htmlAnchor}">
<#if docComments.isDeprecated>
      <span class="deprecated">(deprecated) </span>
      <del>
</#if>
        <i>Enumeration</i> ${symbol.name}
<#if docComments.isDeprecated>
      </del>
</#if>
    </h2>
    <@doc_comments docComments 2, false/>

    <div class="code">
      <table>
        <tr><td colspan=3>enum <@symbol_reference typeSymbol/> ${symbol.name}</td></tr>
        <tr><td>{</td><td rowspan="${items?size+1}">&nbsp;</td><td></td></tr>
<#list items as item>
        <tr>
          <td class="indent"><@symbol_reference item.symbol/></td>
          <td>= ${item.value}<#if item_has_next>,</#if></td>
        </tr>
</#list>
        <tr><td colspan=3>};</td></tr>
      </table>
    </div>

    <h3>Item Details</h3>

    <dl>
<#list items as item>
      <dt class="memberItem"><a class="anchor" id="${item.symbol.htmlLink.htmlAnchor}">${item.symbol.name}:</a></dt>
      <dd class="memberDetail">
        <@doc_comments item.docComments, 4/>
  <#list item.seeSymbols as seeSymbol>
        <div class="docuTag"><span>see: </span>case <@symbol_reference seeSymbol.memberSymbol/> <#rt>
          <#lt>in choice <@symbol_reference seeSymbol.typeSymbol/></div>
  </#list>
      </dd>
</#list>
    </dl>
    <@used_by usedByList/>
<#if collaborationDiagramSvg??>

    <@collaboration_diagram collaborationDiagramSvg/>
</#if>
