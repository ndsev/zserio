<#include "doc_comment.html.ftl">
<#include "linkedtype.html.ftl">
<#include "usedby.html.ftl">
<#include "collaboration_diagram.html.ftl">

    <div class="msgdetail" id="${linkedType.hyperlinkName}">
<#if docComment.isDeprecated>
      <span class="deprecated">(deprecated) </span>
      <del>
</#if>
        <i>bitmask</i> ${type.name}
<#if docComment.isDeprecated>
      </del>
</#if>
    </div>
    <p/>
    <@doc_comment docComment/>

    <table>
    <tr><td class="docuCode">
      <table>

      <tr><td colspan=3>bitmask ${bitmaskType} ${type.name}</td></tr>
      <tr><td>{</td><td rowspan="${type.values?size+1}">&nbsp;</td><td></td></tr>
<#list values as value>
          <tr>
            <td id="tabIndent"><a href="#${value.name}" class="fieldLink">${value.name}</a></td>
            <td>= ${value.value}<#if value_has_next>,</#if></td>
      </tr>
</#list>
      <tr><td colspan=3>};</td></tr>
      </table>
    </td></tr>
    </table>

    <h2>Value Details</h2>

    <dl>
<#list values as value>
      <dt class="memberItem"><a name="${value.name}">${value.name}:</a></dt>
      <dd class="memberDetail">
      <@doc_comment value.docCommentData/>
  <#list value.usageInfoList as usageInfo>
    <#if usageInfo.isFromChoiceCase >
        <div class="docuTag"><span>see: </span><a href="${usageInfo.choiceCaseLink}">${usageInfo.choiceCaseLinkText}</a></div>
    </#if>
  </#list>
      </dd>
</#list>
    </dl>

<@usedby containers services/>
<#if collaborationDiagramSvgFileName??>

    <@collaboration_diagram collaborationDiagramSvgFileName/>
</#if>
