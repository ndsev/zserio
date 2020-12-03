<#ftl output_format="HTML">
<#include "doc_comment.inc.ftl">
<#include "compound.inc.ftl">
<#include "code.inc.ftl">
<#include "usedby.inc.ftl">
<#include "svg_diagram.inc.ftl">
<#assign indent = 5>
<#assign I>${""?left_pad(indent * 2)}</#assign>

${I}<h2 class="anchor" id="${symbol.htmlLink.htmlAnchor}">
${I}  <span<#if docComments.isDeprecated> class="deprecated"</#if>>
${I}    Structure<#if templateParameters?has_content> template</#if> ${symbol.name}
${I}  </span>
${I}</h2>
    <@doc_comments docComments, indent/>

<#assign columnCount=(fields?has_content)?then(3, (functions?has_content)?then(2, 1))/>
    <@code_table_begin indent/>
${I}  <thead>
${I}    <tr>
${I}      <td colspan=${columnCount}>
${I}        struct <@symbol_reference symbol/><@compound_template_parameters templateParameters/><#rt>
              <#lt><@compound_parameters parameters/><#nt>
            <@doc_button indent+4/>
${I}      </td>
${I}    </tr>
${I}    <tr><td colspan=${columnCount}>{</td></tr>
${I}  </thead>
      <@compound_fields fields, columnCount, indent+1/>
<#if functions?has_content>
    <#if fields?has_content>
${I}  <tbody><tr><td colspan=${columnCount}>&nbsp;</td></tr></tbody>
    </#if>
      <@compound_functions functions, columnCount, indent+1/>
</#if>
${I}  <tfoot>
${I}    <tr><td colspan=${columnCount}>};</td></tr>
${I}  </tfoot>
    <@code_table_end indent/>
    <@used_by symbol, usedBySymbols, indent/>
<#if collaborationDiagramSvg??>

    <@collaboration_diagram symbol, collaborationDiagramSvg, indent/>
</#if>
