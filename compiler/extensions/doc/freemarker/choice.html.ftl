<#include "doc_comment.html.ftl">
<#include "linkedtype.html.ftl">
<#include "param.html.ftl">
<#include "usedby.html.ftl">
<#include "collaboration_diagram.html.ftl">
<html>
  <head>
    <title>${categoryPlainText} ${packageName}.${type.name}</title>
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
        <i>${categoryPlainText}</i> ${type.name}
<#if isDeprecated>
      </del>
</#if>
    </div>
    <p/>
    <@doc_comment docComment/>

    <#if choiceData.defaultMember??>
        <#assign rowspanNumber=((choiceData.caseMemberList?size+1)*2)+1/>
    <#else>
        <#assign rowspanNumber=(choiceData.caseMemberList?size*2)+1/>
    </#if>
    <table>
    <tr><td class="docuCode">
      <table style="empty-cells:show">
      <tbody id="tabIndent">
        <tr><td colspan=4>${categoryKeyword} ${type.name}<@parameterlist type/> on ${choiceData.selector}</td></tr>
        <tr><td colspan=2>{</td>
            <td rowspan="${rowspanNumber}">&nbsp;</td>
            <td></td></tr>
<#list choiceData.caseMemberList as caseMember>
        <tr>
          <td valign="top" id="tabIndent">
        <#list caseMember.caseList as case>
            <a name="casedef_${case.expression}"></a>
            <a href="#case_${case.expression}" class="fieldLink">case ${case.expression}</a>:<br/>
        </#list>
          </td>
          <td colspan=3></td>
        </tr>
        <tr>
          <td></td>
    <#if caseMember.compoundField?has_content>
          <td valign="bottom">
      <#assign fname = caseMember.compoundField.name>
      <#assign array = getFieldEmitter(caseMember.compoundField).arrayRange!"">
      <#assign opt = caseMember.compoundField.optionalClause!"">
      <#assign c = caseMember.compoundField.constraint!"">
            <@linkedtype toLinkedType(caseMember.compoundField.fieldType)/><@arglist caseMember.compoundField/>
          </td>
          <td valign="bottom">
            <a href="#${fname}" class="fieldLink">${fname}</a>${array}${opt}${c};
          </td>
    <#else>
          <td colspan=2>;</td>
    </#if>
        </tr>
</#list>
<#if choiceData.defaultMember??>
        <tr>
          <td valign="top" id="tabIndent">
            <a href="#case_default" class="fieldLink">default</a>:<br/>
          </td>
          <td colspan=3></td>
        </tr>
        <tr>
          <td></td>
    <#if choiceData.defaultMember.compoundField?has_content>
          <td valign="bottom">
      <#assign fname = choiceData.defaultMember.compoundField.name>
      <#assign array = getFieldEmitter(choiceData.defaultMember.compoundField).arrayRange!"">
      <#assign opt = choiceData.defaultMember.compoundField.optionalClause!"">
      <#assign c = choiceData.defaultMember.compoundField.constraint!"">
            <@linkedtype toLinkedType(choiceData.defaultMember.compoundField.fieldType)/><@arglist choiceData.defaultMember.compoundField/>
          </td>
          <td valign="bottom">
            <a href="#${fname}" class="fieldLink">${fname}</a>${array}${opt}${c};
          </td>
    <#else>
          <td colspan=2>;</td>
    </#if>
        </tr>
</#if>
<#if functions?has_content>
      </tbody>
      </table>
      <table>
      <tbody id="tabIndent">
  <#list functions as function>
        <tr><td colspan=4 id="tabIndent">&nbsp;</td></tr>
        <tr>
          <td colspan=4 valign="top" id="tabIndent">function ${function.returnTypeName} ${function.funtionType.name}()</td>
        </tr>
        <tr><td colspan=4 id="tabIndent">{</td></tr>
        <tr>
          <td valign="top" id="tabIndent2">return</td>
          <td colspan=3>${function.result};</td></tr>
        <tr><td colspan=4 id="tabIndent">}</td></tr>
  </#list>
</#if>
        <tr><td colspan=4>};</td></tr>
      </tbody>
      </table>
    </td></tr>
    </table>

    <h2 class="msgdetail">Case and Member Details</h2>

    <dl>
<#list choiceData.caseMemberList as caseMember>

        <dt class="memberItem">
            Case(s):
        </dt>
        <dd>
            <dl>
    <#list caseMember.caseList as case>
                <dt class="memberItem">
                    <a name="case_${case.expression}">${case.expression}</a>
                </dt>
                <dd class="memberDetail">
                    <@doc_comment case.docComment/>
                    <#if case.seeLink??>
                      <div class="docuTag"><span>see: </span><a href="${case.seeLink.link}">${case.seeLink.text}</a></div>
                    </#if>
                </dd>
    </#list>
            </dl>
        </dd>
      <dt class="memberItem">
          Member:
      </dt>
      <dd>
          <dl>
  <#if caseMember.compoundField?has_content>
              <dt class="memberItem">
                  <a name="${caseMember.compoundField.name}">${caseMember.compoundField.name}:</a>
              </dt>
              <dd class="memberDetail">
                  <@doc_comment getFieldDocComment(caseMember.compoundField)/>
              </dd>
  <#else>
              <dt class="memberItem">
                  <a name="no_field">no member data</a>
              </dt>
              <dd class="memberDetail">
                  <br />
              </dd>
  </#if>
          </dl>
      </dd>
      <dd>
        <br />
      </dd>
</#list>
<#if choiceData.defaultMember??>
        <dt class="memberItem">
            Case:
        </dt>
        <dd>
            <dl>
                <dt class="memberItem">
                    <a name="case_default">default</a>
                </dt>
                <dd class="memberDetail">
                    Default when no other cases are matched.
                </dd>
            </dl>
        </dd>
      <dt class="memberItem">
          Member:
      </dt>
      <dd>
          <dl>
  <#if choiceData.defaultMember.compoundField?has_content>
              <dt class="memberItem">
                  <a name="${choiceData.defaultMember.compoundField.name}">${choiceData.defaultMember.compoundField.name}:</a>
              </dt>
              <dd class="memberDetail">
                  <@doc_comment getFieldDocComment(choiceData.defaultMember.compoundField)/>
              </dd>
  <#else>
              <dt class="memberItem">
                  <a name="no_field">no member data</a>
              </dt>
              <dd class="memberDetail">
                  <br />
              </dd>
  </#if>
          </dl>
      </dd>
      <dd>
        <br />
      </dd>
</#if>
    </dl>

<@usedby containers/>
<#if collaborationDiagramSvgFileName??>

    <@collaboration_diagram collaborationDiagramSvgFileName/>
</#if>

  </body>
</html>
