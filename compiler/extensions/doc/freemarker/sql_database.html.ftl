<#ftl output_format="HTML">
<#include "doc_comment.inc.ftl">
<#include "compound.inc.ftl">
<#include "code.inc.ftl">
<#include "usedby.inc.ftl">
<#include "svg_diagram.inc.ftl">
<#assign indent = 5>
<#assign I>${""?left_pad(indent * 2)}</#assign>

${I}<h2 class="anchor" id="${symbol.htmlLink.htmlAnchor}">
<#if docComments.isDeprecated>
${I}  <span class="deprecated">(deprecated) </span>
${I}  <del><i>SQL Database</i> ${symbol.name}</del>
<#else>
${I}  <i>SQL Database</i> ${symbol.name}
</#if>
${I}</h2>
    <@doc_comments docComments, indent, false/>

    <@code_table_begin indent/>
<#assign columnCount=(fields?has_content)?then(3, 1)/>
${I}  <tr><td colspan=${columnCount}>sql_database ${symbol.name}</td></tr>
${I}  <tr><td colspan=${columnCount}>{</td></tr>
      <@compound_fields fields, columnCount, indent+1/>
${I}  <tr><td colspan=${columnCount}>};</td></tr>
    <@code_table_end indent/>
    <@compound_member_details symbol, fields, indent/>
<#if collaborationDiagramSvg??>

    <@collaboration_diagram symbol, collaborationDiagramSvg, indent/>
</#if>
