<#ftl output_format="HTML">
<#include "doc_comment.inc.ftl">
<#include "symbol.inc.ftl">
<#include "usedby.inc.ftl">
<#include "collaboration_diagram.inc.ftl">

    <div class="msgdetail" id="${anchorName}">
<#if docComments.isDeprecated>
      <span class="deprecated">(deprecated) </span>
      <del><i>Subtype</i> ${name}</del>
<#else>
      <i>Subtype</i> ${name}
</#if>
    </div>
    <p/>
    <@doc_comments docComments 2 false/>

    <table>
    <tr><td class="docuCode">
      <table>
        <tr>
          <td colspan=3>subtype <@symbol_reference symbol/> ${name};</td>
        </tr>
      </table>
    </td></tr>
    </table>
    <@used_by usedByList/>
<#if collaborationDiagramSvgFileName??>

    <@collaboration_diagram collaborationDiagramSvgFileName/>
</#if>
