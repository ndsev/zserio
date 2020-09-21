<#include "doc_comment.html.ftl">
<#include "linkedtype.html.ftl">
<#include "usedby.html.ftl">
<#include "collaboration_diagram.html.ftl">

    <div class="msgdetail" id="${anchorName}">
<#if docComment.isDeprecated>
      <span class="deprecated">(deprecated) </span>
      <del>
</#if>
      <i>const</i> ${name}
<#if docComment.isDeprecated>
      </del>
</#if>
    </div>
    <p/>
    <@doc_comment docComment/>

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

<@usedby_new usedByList/>
<#if collaborationDiagramSvgFileName??>

    <@collaboration_diagram collaborationDiagramSvgFileName/>
</#if>
