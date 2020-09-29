<#ftl output_format="HTML">
<#include "doc_comment.inc.ftl">
<#include "linkedtype.inc.ftl">
<#include "usedby.inc.ftl">
<#include "collaboration_diagram.inc.ftl">

    <div class="msgdetail" id="${anchorName}">
<#if docComment.isDeprecated>
      <span class="deprecated">(deprecated) </span>
      <del>
</#if>
        <i>Subtype</i> ${name}
<#if docComment.isDeprecated>
      </del>
</#if>
    </div>
    <p/>
    <@doc_comment docComment false/>

    <table>
    <tr><td class="docuCode">
      <table>
        <tr>
          <td colspan=3>subtype <@linkedtype linkedType/> ${name};</td>
        </tr>
      </table>
    </td></tr>
    </table>
    <@used_by usedByList/>
<#if collaborationDiagramSvgFileName??>

    <@collaboration_diagram collaborationDiagramSvgFileName/>
</#if>
