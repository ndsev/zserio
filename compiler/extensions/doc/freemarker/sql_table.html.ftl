<#ftl output_format="HTML">
<#include "doc_comment.inc.ftl">
<#include "compound.inc.ftl">
<#include "code.inc.ftl">
<#include "usedby.inc.ftl">
<#include "svg_diagram.inc.ftl">
<#assign indent = 5>
<#assign I>${""?left_pad(indent * 2)}</#assign>
<#assign sqlTableHeading>
    <#if virtualTableUsing?has_content>virtual </#if><i>SQL Table</i><#t>
      <#if templateParameters?has_content> template</#if> ${symbol.name}<#t>
</#assign>

${I}<h2 class="anchor" id="${symbol.htmlLink.htmlAnchor}">
<#if docComments.isDeprecated>
${I}  <span class="deprecated">(deprecated) </span>
${I}  <del>${sqlTableHeading}</del>
<#else>
${I}  ${sqlTableHeading}
</#if>
${I}</h2>
    <@doc_comments docComments, indent, false/>

    <@code_table_begin indent/>
<#assign columnCount=(fields?has_content)?then(3, (sqlConstraint?has_content)?then(2, 1))/>
${I}  <tr><td colspan=${columnCount}>sql_table ${symbol.name}<@compound_template_parameters templateParameters/><#rt>
        <@compound_parameters parameters/><#t>
        <#lt><#if virtualTableUsing?has_content> using ${virtualTableUsing}</#if></td></tr>
${I}  <tr><td colspan=${columnCount}>{</td></tr>
      <@compound_fields fields, columnCount, indent+1/>
<#if sqlConstraint?has_content>
${I}  <tr><td colspan=${columnCount}>&nbsp;</td></tr>
${I}  <tr>
${I}    <td class="indent"></td>
${I}    <td colspan=${columnCount-1}>sql ${sqlConstraint};</td>
${I}  </tr>
</#if>
${I}  <tr><td colspan=${columnCount}>};</td></tr>
    <@code_table_end indent/>
    <@compound_member_details symbol, fields, indent/>
    <@used_by symbol, usedBySymbols, indent/>
<#if collaborationDiagramSvg??>

    <@collaboration_diagram symbol, collaborationDiagramSvg, indent/>
</#if>
