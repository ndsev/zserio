<#ftl output_format="HTML">
<#include "doc_comment.inc.ftl">
<#include "symbol.inc.ftl">
<#include "usedby.inc.ftl">
<#include "collaboration_diagram.inc.ftl">

    <div class="msgdetail" id="${anchorName}">
<#if docComments.isDeprecated>
      <span class="deprecated">(deprecated) </span>
      <del>
</#if>
        <i>enum</i> ${name}
<#if docComments.isDeprecated>
      </del>
</#if>
    </div>
    <@doc_comments docComments 2 false/>

    <table>
      <tr><td class="docuCode">
        <table>
          <tr><td colspan=3>enum <@symbol_reference typeSymbol/> ${name}</td></tr>
          <tr><td>{</td><td rowspan="${items?size+1}">&nbsp;</td><td></td></tr>
<#list items as item>
          <tr>
            <td id="tabIndent"><@symbol_reference item.symbol/></td>
            <td>= ${item.value}<#if item_has_next>,</#if></td>
          </tr>
</#list>
          <tr><td colspan=3>};</td></tr>
        </table>
      </td></tr>
    </table>

    <h3>Item Details</h3>

    <dl>
<#list items as item>
      <dt class="memberItem"><a name="${item.symbol.htmlLink.htmlAnchor}">${item.symbol.name}:</a></dt>
      <dd class="memberDetail">
        <@doc_comments item.docComments 4/>
  <#list item.seeSymbols as seeSymbol>
        <div class="docuTag"><span>see: </span><@symbol_reference seeSymbol/></div>
  </#list>
      </dd>
</#list>
    </dl>
    <@used_by usedByList/>
<#if collaborationDiagramSvg??>

    <@collaboration_diagram collaborationDiagramSvg/>
</#if>
