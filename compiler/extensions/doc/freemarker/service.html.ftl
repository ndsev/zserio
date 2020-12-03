<#ftl output_format="HTML">
<#include "doc_comment.inc.ftl">
<#include "code.inc.ftl">
<#include "symbol.inc.ftl">
<#include "svg_diagram.inc.ftl">
<#assign indent = 5>
<#assign I>${""?left_pad(indent * 2)}</#assign>

${I}<h2 class="anchor" id="${symbol.htmlLink.htmlAnchor}">
${I}  <span<#if docComments.isDeprecated> class="deprecated"</#if>>Service ${symbol.name}</span>
${I}</h2>
    <@doc_comments docComments, indent/>

    <@code_table_begin indent/>
${I}  <thead>
${I}    <tr><td>
${I}      service <@symbol_reference symbol/>
          <@doc_button indent+3/>
${I}    </td></tr>
${I}    <tr><td>{</td></tr>
${I}  </thead>
<#list methodList as method>
${I}  <tbody>
<#if method.docComments.commentsList?has_content>
${I}    <tr class="doc"><td class="indent">
          <@doc_comments method.docComments, indent+3, true/>
${I}    </td></tr>
    </#if>
${I}    <tr><td class="indent">
${I}      <@symbol_reference method.responseSymbol/> <@symbol_reference method.symbol/><#rt>
            <#lt>(<@symbol_reference method.requestSymbol/>);
${I}    </td></tr>
${I}  </tbody>
</#list>
${I}  <tfoot>
${I}    <tr><td>};</td></tr>
${I}  </tfoot>
    <@code_table_end indent/>
<#if collaborationDiagramSvg??>

    <@collaboration_diagram symbol, collaborationDiagramSvg, indent/>
</#if>
