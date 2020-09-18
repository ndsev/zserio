<#include "doc_comment.html.ftl">
<#include "linkedtype.html.ftl">
<#include "usedby.html.ftl">
<#include "collaboration_diagram.html.ftl">

    <div class="msgdetail" id="${linkedType.hyperlinkName}">
<#if isDeprecated>
      <span class="deprecated">(deprecated) </span>
      <del>
</#if>
      <i>const</i> ${typeName}
<#if isDeprecated>
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
            const <@linkedtype constType/> ${typeName} = ${typeValue};
          </td>
        </tr>
      </table>
    </td></tr>
    </table>

<@usedby containers services/>
<#if collaborationDiagramSvgFileName??>

    <@collaboration_diagram collaborationDiagramSvgFileName/>
</#if>
