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
${I}    <#if virtualTableUsing?has_content>virtual </#if>SQL Table<#rt>
          <#lt><#if templateParameters?has_content> template</#if> ${symbol.name}
${I}  </span>
${I}</h2>
    <@doc_comments docComments, indent/>

<#assign columnCount=(fields?has_content)?then(3, (sqlConstraint?has_content)?then(2, 1))/>
    <@code_table_begin indent/>
${I}  <thead>
${I}    <tr>
${I}      <td colspan=${columnCount}>
${I}        sql_table ${symbol.name}<#rt>
              <@compound_template_parameters templateParameters/><@compound_parameters parameters/><#t>
              <#lt><#if virtualTableUsing?has_content> using ${virtualTableUsing}</#if>
            <@doc_button indent+4/>
${I}      </td>
${I}    </tr>
${I}    <tr><td colspan=${columnCount}>{</td></tr>
${I}  </thead>
      <@compound_fields fields, columnCount, indent+1/>
<#if sqlConstraint?has_content>
${I}  <tbody>
${I}    <tr><td colspan=${columnCount}>&nbsp;</td></tr>
${I}    <tr>
${I}      <td class="indent"></td>
${I}      <td colspan=${columnCount-1}>sql ${sqlConstraint};</td>
${I}    </tr>
${I}  </tbody>
</#if>
${I}  <tfoot>
${I}    <tr><td colspan=${columnCount}>};</td></tr>
${I}  </tfoot>
    <@code_table_end indent/>
    <@used_by symbol, usedBySymbols, indent/>
<#if collaborationDiagramSvg??>

    <@collaboration_diagram symbol, collaborationDiagramSvg, indent/>
</#if>
