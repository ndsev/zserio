<#ftl output_format="HTML">
<#include "doc_comment.inc.ftl">
<#include "compound.inc.ftl">
<#include "symbol.inc.ftl">
<#include "usedby.inc.ftl">
<#include "collaboration_diagram.inc.ftl">
<#macro choice_field field>
    <#local typePrefix>
      <#if field.isArrayImplicit>implicit </#if><#t>
    </#local>
            <tr class="codeMember">
              <td id="tabIndent"></td>
              <td id="tabIndent">
                 ${typePrefix}<@symbol_reference field.typeSymbol/><@compound_field_arguments field.arguments/>
              </td>
              <td>
                <@symbol_reference field.symbol/><#rt>
                  ${field.arrayRange}<#t>
    <#if field.initializerExpression?has_content>
                  <#lt> = ${field.initializerExpression}<#rt>
    </#if>
    <#if field.optionalExpression?has_content>
                  <#lt> if ${field.optionalClauseExpression}<#rt>
    </#if>
    <#if field.constraintExpression?has_content>
                  <#lt> : ${field.constraintExpression}<#rt>
    </#if>
    <#if field.sqlConstraintExpression?has_content>
                  <#lt> sql ${field.sqlConstraintExpression}<#rt>
    </#if>
                  <#lt>;
              </td>
            </tr>
</#macro>

    <div class="msgdetail" id="${anchorName}">
<#if docComments.isDeprecated>
      <span class="deprecated">(deprecated) </span>
      <del><i>Choice</i> ${name}</del>
<#else>
      <i>Choice</i> ${name}
</#if>
    </div>
    <@doc_comments docComments 2 false/>

    <table>
      <tr><td class="docuCode">
        <table>
          <tbody id="tabIndent">
            <tr>
              <td colspan=3>choice ${name}<@compound_parameters parameters/> on ${selectorExpression}</td>
            </tr>
            <tr>
              <td colspan=3>{</td>
            </tr>
<#list caseMemberList as caseMember>
            <tr>
              <td id="tabIndent"></td>
              <td colspan=2>
    <#list caseMember.caseList as case>
                case <@symbol_reference case.detailSymbol/>:<#rt>
                  <#lt><#if case?has_next><br/></#if>
    </#list>
              </td>
            </tr>
    <#if caseMember.field??>
            <@choice_field caseMember.field/>
    <#else>
            <tr>
              <td colspan=2 id="tabIndent"></td>
              <td>;</td>
            </tr>
    </#if>
</#list>
<#if defaultMember??>
            <tr>
              <td id="tabIndent"></td>
              <td colspan=2>
                <a href="#${anchorName}_case_default" class="fieldLink">default</a>:
              </td>
            </tr>
    <#if defaultMember.field??>
            <@choice_field defaultMember.field/>
    <#else>
            <tr>
              <td colspan=2 id="tabIndent"></td>
              <td>;</td>
            </tr>
    </#if>
</#if>
<#if functions?has_content>
            <tr><td colspan=3 id="tabIndent">&nbsp;</td></tr>
            <@compound_functions functions/>
</#if>
            <tr><td colspan=3>};</td></tr>
          </tbody>
        </table>
      </td></tr>
    </table>

    <h3>Case and Member Details</h3>

    <dl>
<#list caseMemberList as caseMember>
      <dt class="memberItem">
        Case(s):
      </dt>
      <dd>
        <dl>
    <#list caseMember.caseList as case>
          <dt class="memberItem">
            <a name="${case.detailSymbol.htmlLink.htmlAnchor}">${case.expression}</a>
          </dt>
          <dd class="memberDetail">
            <@doc_comments case.docComments 6/>
        <#if case.seeSymbol??>
            <div class="docuTag"><span>see: </span>item <@symbol_reference case.seeSymbol.memberSymbol/> <#rt>
              <#lt>in enum <@symbol_reference case.seeSymbol.typeSymbol/></div>
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
            <a name="${caseMember.field.symbol.htmlLink.htmlAnchor}">${caseMember.field.symbol.name}:</a>
          </dt>
          <dd class="memberDetail">
            <@doc_comments caseMember.field.docComments 6/>
          </dd>
  <#else>
          <dt class="memberItem">
            <a name="${anchorName}_no_field">no member data</a>
          </dt>
          <dd class="memberDetail">
            <br/>
          </dd>
  </#if>
        </dl>
      </dd>
</#list>
<#if defaultMember??>
      <dt class="memberItem">
        Case:
      </dt>
      <dd>
        <dl>
          <dt class="memberItem">
            <a name="${anchorName}_case_default">default</a>
          </dt>
          <dd class="memberDetail">
            <@doc_comments defaultMember.docComments 6/>
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
            <a name="${defaultMember.field.symbol.htmlLink.htmlAnchor}">${defaultMember.field.symbol.name}:</a>
          </dt>
          <dd class="memberDetail">
            <@doc_comments defaultMember.field.docComments 6/>
          </dd>
  <#else>
          <dt class="memberItem">
            <a name="${anchorName}_no_field">no member data</a>
          </dt>
          <dd class="memberDetail">
            <br/>
          </dd>
  </#if>
        </dl>
      </dd>
</#if>
    </dl>
    <@compound_function_details functions/>
    <@used_by usedByList/>
<#if collaborationDiagramSvg??>

    <@collaboration_diagram collaborationDiagramSvg/>
</#if>
