<#ftl output_format="HTML">
<#include "doc_comment.inc.ftl">
<#include "symbol.inc.ftl">
<#include "usedby.inc.ftl">
<#include "collaboration_diagram.inc.ftl">

    <div class="msgdetail" id="${symbol.htmlLink.htmlAnchor}">
<#if docComments.isDeprecated>
      <span class="deprecated">(deprecated) </span>
      <del><i>bitmask</i> ${symbol.name}</del>
<#else>
      <i>bitmask</i> ${symbol.name}
</#if>
    </div>
    <@doc_comments docComments 2 false/>

    <table>
      <tr><td class="docuCode">
        <table>
          <tr><td colspan=3>bitmask <@symbol_reference typeSymbol/> ${symbol.name}</td></tr>
          <tr><td>{</td><td rowspan="${values?size+1}">&nbsp;</td><td></td></tr>
<#list values as value>
          <tr>
            <td id="tabIndent"><@symbol_reference value.symbol/></td>
            <td>= ${value.value}<#if value_has_next>,</#if></td>
          </tr>
</#list>
          <tr><td colspan=3>};</td></tr>
        </table>
      </td></tr>
    </table>

    <h3>Value Details</h3>

    <dl>
<#list values as value>
      <dt class="memberItem"><a name="${value.symbol.htmlLink.htmlAnchor}">${value.symbol.name}:</a></dt>
      <dd class="memberDetail">
        <@doc_comments value.docComments 4/>
  <#list value.seeSymbols as seeSymbol>
        <div class="docuTag"><span>see: </span>case <@symbol_reference seeSymbol.memberSymbol/> <#rt>
          <#lt>(<@symbol_reference seeSymbol.typeSymbol/>)</div>
  </#list>
      </dd>
</#list>
    </dl>
    <@used_by usedByList/>
<#if collaborationDiagramSvgUrl??>

    <@collaboration_diagram collaborationDiagramSvgUrl/>
</#if>
