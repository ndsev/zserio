<#include "doc_comment.html.ftl">
<#include "linkedtype.html.ftl">
<#include "usedby.html.ftl">
<#include "collaboration_diagram.html.ftl">

    <div class="msgdetail" id="${linkedType.hyperlinkName}">
<#if isDeprecated>
      <span class="deprecated">(deprecated) </span>
      <del>
</#if>
        <i>Subtype</i> ${type.name}
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
          <td colspan=3>subtype <@linkedtype targetType/><#--@arglist targetType--> ${type.name};</td>
        </tr>
      </table>
    </td></tr>
    </table>

<@usedby containers services/>
<#if collaborationDiagramSvgFileName??>

    <@collaboration_diagram collaborationDiagramSvgFileName/>
</#if>

    <h4>Const instances</h4>
    <table>
    <tr><td class="docuCode">
      <table>
      <tbody id="tabIndent">
<#assign numOfConstInstances = constInstances?size>
<#if (numOfConstInstances > 0)>
    <#list constInstances as constInstance>
        <tr><td><@linkedtype constInstance/></td></tr>
    </#list>
<#else>
        <tr><td><div class="docuTag">&lt;<i>no const values for this subtype exists.</i>&gt;</div></td></tr>
</#if>
      </tbody>
      </table>
    </td></tr>
    </table>
