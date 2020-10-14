<#ftl output_format="HTML">
<#include "doc_comment.inc.ftl">
<#include "symbol.inc.ftl">
<#include "usedby.inc.ftl">
<#include "svg_diagram.inc.ftl">

    <div class="msgdetail" id="${symbol.htmlLink.htmlAnchor}">
<#if docComments.isDeprecated>
      <span class="deprecated">(deprecated) </span>
      <del><i>const</i> ${symbol.name}</del>
<#else>
      <i>const</i> ${symbol.name}
</#if>
    </div>
    <@doc_comments docComments, 2, false/>

    <table>
      <tr><td class="docuCode">
        <table>
          <tr>
            <td colspan=3>
              const <@symbol_reference typeSymbol/> ${symbol.name} = ${value};
            </td>
          </tr>
        </table>
      </td></tr>
    </table>
    <@used_by usedByList/>
<#if collaborationDiagramSvg??>

    <@collaboration_diagram collaborationDiagramSvg/>
</#if>
