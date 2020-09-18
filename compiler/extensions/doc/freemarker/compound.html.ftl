<#include "doc_comment.html.ftl">
<#include "linkedtype.html.ftl">
<#include "param.html.ftl">
<#include "usedby.html.ftl">
<#include "collaboration_diagram.html.ftl">

    <div class="msgdetail" id="${linkedType.hyperlinkName}">
<#if isDeprecated>
        <span class="deprecated">(deprecated) </span><del>
</#if>
        <i>${categoryPlainText}</i>
<#if virtualTableUsing?has_content>
        <i>VIRTUAL </i>
</#if>
        ${type.name}
<#if virtualTableUsing?has_content>
        <i>USING</i> ${virtualTableUsing}
</#if>
<#if isDeprecated>
        </del>
</#if>
    </div>
    <p/>
    <@doc_comment docComment/>

    <table>
    <tr><td class="docuCode">
      <table>
      <tbody id="tabIndent">
        <tr><td colspan=4>${categoryKeyword} ${type.name}<@parameterlist type/></td></tr>
        <tr><td colspan=4>{</td></tr>
<#list fields as field>
  <#assign fname = field.name>
  <#assign oname = field.offset>
  <#assign array = field.arrayRange!"">
  <#assign init = field.initializer>
  <#assign opt = field.optionalClause>
  <#assign c = field.constraint>
  <#assign sqlc = field.sqlConstraint>

  <#if field.hasAlignment>
        <tr class="codeMember">
          <td></td><td colspan=2>align(${field.alignmentValue}):</td>
        </tr>

  </#if>
  <#if oname?has_content>
        <tr class="codeMember">
          <td></td><td colspan=2>${oname}</td>
        </tr>
  </#if>
        <tr class="codeMember">
          <td></td>
          <td valign="top" id="tabIndent"><@linkedtype field.type/><@arglist field/></td>
          <td valign="bottom">
            <a href="#${fname}" class="fieldLink">${fname}</a>${array}${init}${opt}${c};</td>
          <td valign="bottom"><i>${sqlc}</i></td>
          <#if field.isVirtual>
            <td valign="bottom"><i>(virtual)</i></td>
          <#elseif field.isAutoOptional>
            <td valign="bottom"><i>(optional)</i></td>
          <#elseif field.isArrayImplicit>
            <td valign="bottom"><i>(implicit)</i></td>
          </#if>
          <td></td>
        </tr>
</#list>
<#if functions?has_content>
      </tbody>
      </table>

      <table>
      <tbody id="tabIndent">
  <#list functions as function>
        <tr><td colspan=3 id="tabIndent">&nbsp;</td></tr>
        <tr>
          <td colspan=3 valign="top" id="tabIndent">function ${function.returnTypeName} ${function.funtionType.name}()</td>
        </tr>
        <tr><td colspan=3 id="tabIndent">{</td></tr>
        <tr>
          <td></td>
          <td valign="top" id="tabIndent2">return</td>
          <td>${function.result};</td></tr>
        <tr><td colspan=3 id="tabIndent">}</td></tr>
  </#list>
</#if>
        <tr><td colspan=3>};</td></tr>
<#assign constraint = sqlConstraint>
  <#if constraint?has_content>
        <td></td>
        <tr><td colspan=3>${constraint};</td></tr>
        <td></td>
  </#if>

      </tbody>
      </table>
    </td></tr>
    </table>


    <h2 class="msgdetail">Member Details</h2>

    <dl>
<#list fields as field>
      <dt class="memberItem"><a name="${field.name}">
  <#if field.isDeprecated>(deprecated) <del></#if>
        ${field.name}
  <#if field.isDeprecated></del></#if> :
      </a></dt>
      <dd class="memberDetail">
      <@doc_comment field.docComment/>
      </dd>
</#list>
    </dl>

<@usedby containers services/>
<#if collaborationDiagramSvgFileName??>

    <@collaboration_diagram collaborationDiagramSvgFileName/>
</#if>
