<#ftl output_format="HTML">
<#include "doc_comment.inc.ftl">
<#include "linkedtype.inc.ftl">
<#include "usedby.inc.ftl">
<#include "collaboration_diagram.inc.ftl">

    <div class="msgdetail" id="${anchorName}">
<#if docComments.isDeprecated>
      <span class="deprecated">(deprecated) </span>
      <del>
</#if>
        <i>const</i> ${name}
<#if docComments.isDeprecated>
      </del>
</#if>
    </div>
    <p/>
    <@doc_comments docComments false/>

    <table>
    <tr><td class="docuCode">
      <table>
        <tr>
          <td colspan=3>
            const <@linkedtype linkedType/> ${name} = ${value};
          </td>
        </tr>
      </table>
    </td></tr>
    </table>
    <@used_by usedByList/>
<#if collaborationDiagramSvgFileName??>

    <@collaboration_diagram collaborationDiagramSvgFileName/>
</#if>
