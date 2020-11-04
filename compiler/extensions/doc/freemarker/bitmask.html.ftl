<#ftl output_format="HTML">
<#include "doc_comment.inc.ftl">
<#include "symbol.inc.ftl">
<#include "usedby.inc.ftl">
<#include "svg_diagram.inc.ftl">

    <h2 class="anchor" id="${symbol.htmlLink.htmlAnchor}">
<#if docComments.isDeprecated>
      <span class="deprecated">(deprecated) </span>
      <del><i>Bitmask</i> ${symbol.name}</del>
<#else>
      <i>Bitmask</i> ${symbol.name}
</#if>
    </h2>
    <@doc_comments docComments 2, false/>

    <div class="code">
      <table>
        <tr><td colspan=3>bitmask <@symbol_reference typeSymbol/> ${symbol.name}</td></tr>
        <tr><td>{</td><td rowspan="${values?size+1}">&nbsp;</td><td></td></tr>
<#list values as value>
        <tr>
          <td class="indent"><@symbol_reference value.symbol/></td>
          <td>= ${value.value}<#if value_has_next>,</#if></td>
        </tr>
</#list>
        <tr><td colspan=3>};</td></tr>
      </table>
    </div>

    <h3>Value Details</h3>

    <dl>
<#list values as value>
      <dt class="memberItem"><a class="anchor" id="${value.symbol.htmlLink.htmlAnchor}">${value.symbol.name}:</a></dt>
      <dd class="memberDetail">
        <@doc_comments value.docComments, 4/>
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
