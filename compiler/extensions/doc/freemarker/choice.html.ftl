<#include "doc_comment.inc.ftl">
<#include "compound.inc.ftl">
<#include "linkedtype.inc.ftl">
<#include "usedby.inc.ftl">
<#include "collaboration_diagram.inc.ftl">

    <div class="msgdetail" id="${anchorName}">
<#if docComment.isDeprecated>
      <span class="deprecated">(deprecated) </span>
      <del>
</#if>
        <i>Choice</i> ${name}
<#if docComment.isDeprecated>
      </del>
</#if>
    </div>

    <@doc_comment docComment/>

    <#if defaultMember??>
        <#assign rowspanNumber=((caseMemberList?size+1)*2)+1/>
    <#else>
        <#assign rowspanNumber=(caseMemberList?size*2)+1/>
    </#if>
    <table>
    <tr><td class="docuCode">
      <table style="empty-cells:show;">
      <tbody id="tabIndent">
        <tr>
          <td colspan=4>choice ${name}<@compound_parameters parameters/> on ${selectorExpression}</td>
        </tr>
        <tr>
          <td colspan=2>{</td>
          <td rowspan="${rowspanNumber}">&nbsp;</td>
          <td></td>
        </tr>
<#list caseMemberList as caseMember>
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
    <#if caseMember.field??>
          <@compound_field caseMember.field/>
    <#else>
          <td colspan=2>;</td>
    </#if>
        </tr>
</#list>
<#if defaultMember??>
        <tr>
          <td valign="top" id="tabIndent">
            <a href="#case_default" class="fieldLink">default</a>:<br/>
          </td>
          <td colspan=3></td>
        </tr>
        <tr>
          <td></td>
    <#if defaultMember.field??>
          <@compound_field defaultMember.field/>
    <#else>
          <td colspan=2>;</td>
    </#if>
        </tr>
</#if>
<#if functions?has_content>
        <tr><td colspan=4 id="tabIndent">&nbsp;</td></tr>
        <@compound_functions functions 4/>
</#if>
        <tr><td colspan=4>};</td></tr>
      </tbody>
      </table>
    </td></tr>
    </table>

    <h2 class="msgdetail">Case and Member Details</h2>

    <dl>
<#list caseMemberList as caseMember>
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
  <#if caseMember.field??>
              <dt class="memberItem">
                  <a name="${caseMember.field.name}">${caseMember.field.name}:</a>
              </dt>
              <dd class="memberDetail">
                  <@doc_comment caseMember.field.docComment/>
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
<#if defaultMember??>
        <dt class="memberItem">
            Case:
        </dt>
        <dd>
            <dl>
                <dt class="memberItem">
                    <a name="case_default">default</a>
                </dt>
                <dd class="memberDetail">
                    <@doc_comment defaultMember.docComment/>
                </dd>
            </dl>
        </dd>
      <dt class="memberItem">
          Member:
      </dt>
      <dd>
          <dl>
  <#if defaultMember.field??>
              <dt class="memberItem">
                  <a name="${defaultMember.field.name}">${defaultMember.field.name}:</a>
              </dt>
              <dd class="memberDetail">
                  <@doc_comment defaultMember.field.docComment/>
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

<@used_by usedByList/>
<#if collaborationDiagramSvgFileName??>

    <@collaboration_diagram collaborationDiagramSvgFileName/>
</#if>
