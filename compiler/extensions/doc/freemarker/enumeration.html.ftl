<#include "doc_comment.html.ftl">
<#include "linkedtype.html.ftl">
<#include "usedby.html.ftl">
<#include "collaboration_diagram.html.ftl">
<html>
  <head>
    <title>enum ${packageName}.${type.name}</title>
    <link rel="stylesheet" type="text/css" href="../../webStyles.css">
    <meta http-equiv="content-type" content="text/html; charset=UTF-8">
  </head>
  <body>

    <h2>${packageName}</h2>
    <div class="msgdetail">
<#if docComment.isDeprecated>
      <span class="deprecated">(deprecated) </span>
      <del>
</#if>
        <i>enum</i> ${type.name}
<#if docComment.isDeprecated>
      </del>
</#if>
    </div>
    <p/>
    <@doc_comment docComment/>

    <table>
    <tr><td class="docuCode">
      <table>

      <tr><td colspan=3>enum ${enumType} ${type.name}</td></tr>
      <tr><td>{</td><td rowspan="${type.items?size+1}">&nbsp;</td><td></td></tr>
<#list items as item>
          <tr>
            <td id="tabIndent"><a href="#${item.name}" class="fieldLink">${item.name}</a></td>
            <td>= ${item.value}<#if item_has_next>,</#if></td>
      </tr>
</#list>
      <tr><td colspan=3>};</td></tr>
      </table>
    </td></tr>
    </table>

    <h2>Item Details</h2>

    <dl>
<#list items as item>
      <dt class="memberItem"><a name="${item.name}">${item.name}:</a></dt>
      <dd class="memberDetail">
      <@doc_comment item.docCommentData/>
  <#list item.usageInfoList as usageInfo>
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

  </body>
</html>
