<#include "doc_comment.html.ftl">
<#include "linkedtype.html.ftl">
<#include "usedby.html.ftl">
<#include "collaboration_diagram.html.ftl">
<html>
  <head>
    <title>Const ${packageName}.${typeName}</title>
    <link rel="stylesheet" type="text/css" href="../../webStyles.css">
    <meta http-equiv="content-type" content="text/html; charset=UTF-8">
  </head>
  <body>

    <h2>${packageName}</h2>
    <div class="msgdetail">
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

  </body>
</html>
