<#ftl output_format="HTML">
<#include "doc_comment.inc.ftl">
<#include "compound.inc.ftl">
<#include "code.inc.ftl">
<#include "usedby.inc.ftl">
<#include "svg_diagram.inc.ftl">
<#assign indent = 5>
<#assign I>${""?left_pad(indent * 2)}</#assign>

<#if hasFloatingDocComments(docComments)>
    <@doc_comments_floating docComments, indent/>

</#if>
${I}<h2 class="anchor" id="${symbol.htmlLink.htmlAnchor}">
${I}  <span<#if docComments.isDeprecated> class="deprecated"</#if>>SQL Database ${symbol.name}</span>
${I}</h2>

<#assign columnCount=(fields?has_content)?then(3, 1)/>
    <@code_table_begin indent/>
${I}  <thead>
<#if hasStickyDocComments(docComments)>
${I}    <tr class="doc"><td colspan=${columnCount}>
          <@doc_comments_sticky docComments, indent+3/>
${I}    </td></tr>
</#if>
${I}    <tr>
${I}      <td colspan=${columnCount}>
${I}        sql_database <@symbol_reference symbol/>
            <@doc_button indent+4/>
${I}      </td>
${I}    </tr>
${I}    <tr><td colspan=${columnCount}>{</td></tr>
${I}  </thead>
      <@compound_fields fields, columnCount, indent+1/>
${I}  <tfoot>
${I}    <tr><td colspan=${columnCount}>};</td></tr>
${I}  </tfoot>
    <@code_table_end indent/>
<#if collaborationDiagramSvg??>

    <@collaboration_diagram symbol, collaborationDiagramSvg, indent/>
</#if>
