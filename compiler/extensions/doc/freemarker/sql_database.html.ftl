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
${I}  <tr><td colspan=3>sql_database ${symbol.name}</td></tr>
${I}  <tr><td colspan=3>{</td></tr>
      <@compound_fields fields, indent+1/>
${I}  <tr><td colspan=3>};</td></tr>
    <@code_table_end indent/>
    <@compound_member_details fields, indent/>
<#if collaborationDiagramSvg??>

    <@collaboration_diagram collaborationDiagramSvg, indent/>
</#if>
