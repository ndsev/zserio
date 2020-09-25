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
        <i>bitmask</i> ${name}
<#if docComment.isDeprecated>
      </del>
</#if>
    </div>
    <p/>
    <@doc_comment docComment false/>

    <table>
    <tr><td class="docuCode">
      <table>

      <tr><td colspan=3>bitmask <@linkedtype linkedType/> ${name}</td></tr>
      <tr><td>{</td><td rowspan="${values?size+1}">&nbsp;</td><td></td></tr>
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

    <h3>Value Details</h3>

    <dl>
<#list values as value>
      <dt class="memberItem"><a name="${value.name}">${value.name}:</a></dt>
      <dd class="memberDetail">
      <@doc_comment value.docComment/>
  <#list value.usageInfoList as usageInfo>
    <#if usageInfo.isFromChoiceCase >
        <div class="docuTag"><span>see: </span><a href="${usageInfo.choiceCaseLink}">${usageInfo.choiceCaseLinkText}</a></div>
    </#if>
  </#list>
      </dd>
</#list>
    </dl>

<@used_by usedByList/>
<#if collaborationDiagramSvgUrl??>

    <@collaboration_diagram collaborationDiagramSvgUrl/>
</#if>
