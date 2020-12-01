<#ftl output_format="HTML">
<#include "doc_comment.inc.ftl">
<#include "compound.inc.ftl">
<#include "code.inc.ftl">
<#include "symbol.inc.ftl">
<#include "usedby.inc.ftl">
<#include "svg_diagram.inc.ftl">
<#assign indent = 5>
<#assign I>${""?left_pad(indent * 2)}</#assign>
<#macro choice_field field indent>
    <#local I>${""?left_pad(indent * 2)}</#local>
${I}<tbody class="anchor-group" id="${field.symbol.htmlLink.htmlAnchor}">
    <#if field.docComments.commentsList?has_content>
${I}  <tr class="doc">
${I}    <td class="indent empty"></td>
${I}    <td colspan=2 class="indent">
          <@doc_comments field.docComments, indent+3, true/>
${I}    </td>
${I}  </tr>
    </#if>
${I}  <tr>
${I}    <td class="indent empty"></td>
${I}    <td class="indent">
${I}      <#if field.isArrayImplicit>implicit </#if><@compound_field_type_name field/>
${I}    </td>
${I}    <td>
${I}      <@symbol_reference field.symbol/><#rt>
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
${I}    </td>
${I}  </tr>
${I}</tbody>
</#macro>

${I}<h2 class="anchor" id="${symbol.htmlLink.htmlAnchor}">
${I}  <span<#if docComments.isDeprecated> class="deprecated"</#if>>
${I}    Choice<#if templateParameters?has_content> template</#if> ${symbol.name}
${I}  </span>
${I}</h2>
    <@doc_comments docComments, indent/>

<#function choiceColumnCount caseMemberList functions defaultMember>
    <#if defaultMember?has_content && defaultMember.field??>
        <#return 3>
    </#if>
    <#list caseMemberList as caseMember>
        <#if caseMember.field??>
            <#return 3>
        </#if>
    </#list>
    <#if caseMemberList?has_content || functions?has_content || defaultMember?has_content>
        <#return 2>
    </#if>
    <#return 1>
</#function>
<#assign columnCount=choiceColumnCount(caseMemberList, functions, defaultMember!"")/>
    <@code_table_begin indent/>
${I}  <thead>
${I}    <tr>
${I}      <td colspan=${columnCount}>
${I}        choice ${symbol.name}<@compound_template_parameters templateParameters/><#rt>
              <#lt><@compound_parameters parameters/> on ${selectorExpression}
            <@doc_button indent+4/>
${I}      </td>
${I}    </tr>
${I}    <tr><td colspan=${columnCount}>{</td></tr>
${I}  </thead>
<#list caseMemberList as caseMember>
    <#list caseMember.caseList as case>
${I}  <tbody class="anchor-group" id="${case.symbol.htmlLink.htmlAnchor}">
        <#if case.docComments.commentsList?has_content>
${I}    <tr class="doc"><td class="indent empty"></td><td colspan=2>
          <@doc_comments case.docComments, indent+3, true/>
${I}    </td></tr>
        </#if>
${I}    <tr>
${I}      <td class="indent empty"></td>
${I}      <td colspan=${columnCount-1}>
${I}        <@symbol_reference case.symbol/> <@symbol_reference case.expressionSymbol/>:<#rt>
              <#lt><#if case?has_next><br/></#if>
${I}      </td>
${I}    </tr>
${I}  </tbody>
    </#list>
    <#if caseMember.field??>
      <@choice_field caseMember.field, indent+1/>
    <#else>
${I}  <tbody>
${I}    <tr>
${I}      <td class="indent empty"></td>
${I}      <td class="indent" colspan=${columnCount-1}>;</td>
${I}    </tr>
${I}  </tbody>
    </#if>
</#list>
<#if defaultMember??>
${I}  <tbody class="anchor-group" id="${defaultMember.symbol.htmlLink.htmlAnchor}">
    <#if defaultMember.docComments.commentsList?has_content>
${I}    <tr class="doc">
${I}      <td class="indent empty"></td>
${I}      <td colspan=2>
            <@doc_comments defaultMember.docComments, indent+4, true/>
${I}      </td>
${I}    </tr>
    </#if>
${I}    <tr>
${I}      <td class="indent empty"></td>
${I}      <td colspan=${columnCount-1}>
${I}        <@symbol_reference defaultMember.symbol/>:
${I}      </td>
${I}    </tr>
${I}  </tbody>
    <#if defaultMember.field??>
${I}    <@choice_field defaultMember.field, indent+2/>
    <#else>
${I}  <tbody>
${I}    <tr>
${I}      <td class="indent empty"></td>
${I}      <td class="indent" colspan=${columnCount-1}>;</td>
${I}    </tr>
${I}  </tbody>
    </#if>
</#if>
<#if functions?has_content>
${I}  <tbody><tr><td colspan=${columnCount}>&nbsp;</td></tr></tbody>
${I}  <@compound_functions functions, columnCount, indent+1/>
</#if>
${I}  <tfoot>
${I}    <tr><td colspan=${columnCount}>};</td></tr>
${I}  </tfoot>
    <@code_table_end indent/>
    <@used_by symbol, usedBySymbols, indent/>
<#if collaborationDiagramSvg??>

    <@collaboration_diagram symbol, collaborationDiagramSvg, indent/>
</#if>
