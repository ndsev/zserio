<#ftl output_format="HTML">
<#include "doc_comment.inc.ftl">
<#include "compound.inc.ftl">
<#include "code.inc.ftl">
<#include "symbol.inc.ftl">
<#include "usedby.inc.ftl">
<#include "svg_diagram.inc.ftl">
<#assign indent = 5>
<#assign I>${""?left_pad(indent * 2)}</#assign>
<#assign choiceHeading>
    <i>Choice</i><#if templateParameters?has_content> template</#if> ${symbol.name}<#t>
</#assign>
<#macro choice_field field indent>
    <#local I>${""?left_pad(indent * 2)}</#local>
${I}<tr>
${I}  <td class="indent empty"></td>
${I}  <td class="indent">
${I}    <#if field.isArrayImplicit>implicit </#if><@compound_field_type_name field/>
${I}  </td>
${I}  <td>
${I}    <@symbol_reference field.symbol/><#rt>
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
${I}  </td>
${I}</tr>
</#macro>

${I}<h2 class="anchor" id="${symbol.htmlLink.htmlAnchor}">
<#if docComments.isDeprecated>
${I}  <span class="deprecated">(deprecated) </span>
${I}  <del>${choiceHeading}</del>
<#else>
${I}  ${choiceHeading}
</#if>
${I}</h2>
    <@doc_comments docComments, indent, false/>

    <@code_table_begin indent/>
${I}  <tr>
${I}    <td colspan=3>choice ${symbol.name}<@compound_template_parameters templateParameters/><#rt>
          <#lt><@compound_parameters parameters/> on ${selectorExpression}</td>
${I}  </tr>
${I}  <tr>
${I}    <td colspan=3>{</td>
${I}  </tr>
<#list caseMemberList as caseMember>
${I}  <tr>
${I}    <td class="indent empty"></td>
${I}    <td colspan=2>
    <#list caseMember.caseList as case>
${I}      case <@symbol_reference case.symbol/>:<#rt>
            <#lt><#if case?has_next><br/></#if>
    </#list>
${I}    </td>
${I}  </tr>
    <#if caseMember.field??>
      <@choice_field caseMember.field, indent+1/>
    <#else>
${I}  <tr>
${I}    <td colspan=2></td>
${I}    <td>;</td>
${I}  </tr>
    </#if>
</#list>
<#if defaultMember??>
${I}  <tr>
${I}    <td class="indent empty"></td>
${I}    <td colspan=2>
${I}      <@symbol_reference defaultMember.symbol/>:
${I}    </td>
${I}  </tr>
    <#if defaultMember.field??>
${I}  <@choice_field defaultMember.field, indent+1/>
    <#else>
${I}  <tr>
${I}    <td colspan=2></td>
${I}    <td>;</td>
${I}  </tr>
    </#if>
</#if>
<#if functions?has_content>
${I}  <tr><td colspan=3>&nbsp;</td></tr>
${I}  <@compound_functions functions, indent+1/>
</#if>
${I}  <tr><td colspan=3>};</td></tr>
    <@code_table_end indent/>

${I}<h3 class="anchor" id="${symbol.htmlLink.htmlAnchor}_case_member_details">Case and Member Details</h3>

${I}<dl>
<#list caseMemberList as caseMember>
${I}  <dt>
${I}    Case(s):
${I}  </dt>
${I}  <dd>
${I}    <dl>
    <#list caseMember.caseList as case>
${I}      <dt>
${I}        <span class="anchor" id="${case.symbol.htmlLink.htmlAnchor}">${case.expression}</span>
${I}      </dt>
${I}      <dd>
            <@doc_comments case.docComments, indent+4/>
        <#if case.seeSymbol??>
${I}        <div class="doc"><span>see: </span>item <@symbol_reference case.seeSymbol.memberSymbol/> <#rt>
              <#lt>in enum <@symbol_reference case.seeSymbol.typeSymbol/></div>
        </#if>
${I}      </dd>
    </#list>
${I}    </dl>
${I}  </dd>
${I}  <dt>
${I}    Member:
${I}  </dt>
${I}  <dd>
${I}    <dl>
  <#if caseMember.field??>
${I}      <dt>
${I}        <span class="anchor" id="${caseMember.field.symbol.htmlLink.htmlAnchor}">${caseMember.field.symbol.name}:</span>
${I}      </dt>
${I}      <dd>
            <@doc_comments caseMember.field.docComments, indent+4/>
${I}      </dd>
  <#else>
${I}      <dt>
${I}        <span>no member data</span>
${I}      </dt>
${I}      <dd>
${I}        <br/>
${I}      </dd>
  </#if>
${I}    </dl>
${I}  </dd>
</#list>
<#if defaultMember??>
${I}  <dt>
${I}    Case:
${I}  </dt>
${I}  <dd>
${I}    <dl>
${I}      <dt>
${I}        <span class="anchor" id="${defaultMember.symbol.htmlLink.htmlAnchor}">${defaultMember.symbol.name}</span>
${I}      </dt>
${I}      <dd>
            <@doc_comments defaultMember.docComments, indent+4/>
${I}      </dd>
${I}    </dl>
${I}  </dd>
${I}  <dt>
${I}    Member:
${I}  </dt>
${I}  <dd>
${I}    <dl>
  <#if defaultMember.field??>
${I}      <dt>
${I}        <span class="anchor" id="${defaultMember.field.symbol.htmlLink.htmlAnchor}">${defaultMember.field.symbol.name}:</span>
${I}      </dt>
${I}      <dd>
            <@doc_comments defaultMember.field.docComments, indent+4/>
${I}      </dd>
  <#else>
${I}      <dt>
${I}        <span>no member data</span>
${I}      </dt>
${I}      <dd>
${I}        <br/>
${I}      </dd>
  </#if>
${I}    </dl>
${I}  </dd>
</#if>
${I}</dl>
    <@compound_function_details symbol, functions, indent/>
    <@used_by symbol, usedBySymbols, indent/>
<#if collaborationDiagramSvg??>

    <@collaboration_diagram symbol, collaborationDiagramSvg, indent/>
</#if>
