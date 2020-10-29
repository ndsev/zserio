<#ftl output_format="HTML">
<#include "doc_comment.inc.ftl">
<#include "symbol.inc.ftl">
<#include "usedby.inc.ftl">
<#include "svg_diagram.inc.ftl">

    <h2 class="anchor" id="${symbol.htmlLink.htmlAnchor}">
<#if docComments.isDeprecated>
      <span class="deprecated">(deprecated) </span>
      <del><i>Subtype</i> ${symbol.name}</del>
<#else>
      <i>Subtype</i> ${symbol.name}
</#if>
    </h2>
    <@doc_comments docComments 2, false/>

    <table>
    <tr><td class="docuCode">
      <table>
        <tr>
          <td colspan=3>subtype <@symbol_reference typeSymbol/> ${symbol.name};</td>
        </tr>
      </table>
    </td></tr>
    </table>
    <@used_by usedByList/>
<#if collaborationDiagramSvg??>

    <@collaboration_diagram collaborationDiagramSvg/>
</#if>
