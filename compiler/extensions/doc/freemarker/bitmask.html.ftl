<#ftl output_format="HTML">
<#include "doc_comment.inc.ftl">
<#include "code.inc.ftl">
<#include "symbol.inc.ftl">
<#include "usedby.inc.ftl">
<#include "svg_diagram.inc.ftl">
<#assign indent = 5>
<#assign I>${""?left_pad(indent * 2)}</#assign>

<#if hasFloatingDocComments(docComments)>
    <@doc_comments_floating docComments, indent/>

</#if>
${I}<h2 class="anchor" id="${symbol.htmlLink.htmlAnchor}">
${I}  <span<#if docComments.isDeprecated> class="deprecated"</#if>>Bitmask ${symbol.name}</span>
${I}</h2>

    <@code_table_begin indent/>
${I}  <thead>
<#if hasStickyDocComments(docComments)>
${I}    <tr class="doc"><td colspan=2>
          <@doc_comments_sticky docComments, indent+3/>
${I}    </td></tr>
</#if>
${I}    <tr><td colspan=2>
${I}      bitmask <@symbol_reference typeSymbol/> <@symbol_reference symbol/>
          <@doc_button indent+2/>
${I}    </td></tr>
${I}    <tr><td colspan=2>{</td></tr>
${I}  </thead>
<#list values as value>
${I}  <tbody class="anchor-group" id="${value.symbol.htmlLink.htmlAnchor}">
    <#if hasDocComments(value.docComments)>
${I}    <tr class="doc"><td colspan=2 class="indent">
          <@doc_comments_all value.docComments, indent+3/>
${I}    </td></tr>
    </#if>
${I}    <tr>
    <#if value.hasValueExpression>
${I}      <td class="indent"><@symbol_reference value.symbol/></td>
${I}      <td class="value-expression">= ${value.value}<#if value?has_next>,</#if></td>
    <#else>
${I}      <td class="indent"><@symbol_reference value.symbol/><#if value?has_next>,</#if></td><td></td>
    </#if>
${I}    </tr>
${I}  </tbody>
</#list>
${I}  <tfoot>
${I}    <tr><td colspan=2>};</td></tr>
${I}  </tfoot>
    <@code_table_end indent/>
    <@used_by symbol, usedBySymbols, indent/>
<#if collaborationDiagramSvgUrl??>

    <@collaboration_diagram symbol, collaborationDiagramSvgUrl, indent/>
</#if>
