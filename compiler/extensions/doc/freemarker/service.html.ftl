<#ftl output_format="HTML">
<#include "doc_comment.inc.ftl">
<#include "code.inc.ftl">
<#include "symbol.inc.ftl">
<#include "svg_diagram.inc.ftl">
<#assign indent = 5>
<#assign I>${""?left_pad(indent * 2)}</#assign>

${I}<h2 class="anchor" id="${symbol.htmlLink.htmlAnchor}">
<#if docComments.isDeprecated>
${I}  <span class="deprecated">(deprecated) </span>
${I}  <del><i>Service</i> ${symbol.name}</del>
<#else>
${I}  <i>Service</i> ${symbol.name}
</#if>
${I}</h2>
    <@doc_comments docComments, indent, false/>

    <@code_table_begin indent/>
${I}  <tr><td>service ${symbol.name}</td></tr>
${I}  <tr><td>{</td></tr>
<#list methodList as method>
${I}  <tr><td class="indent">
${I}    <@symbol_reference method.responseSymbol/> <@symbol_reference method.symbol/><#rt>
          <#lt>(<@symbol_reference method.requestSymbol/>);
${I}  </td></tr>
</#list>
${I}  <tr><td>};</td></tr>
    <@code_table_end indent/>
<#if methodList?has_content>

${I}<h3 class="anchor" id="${symbol.htmlLink.htmlAnchor}_methods">Service methods</h3>

${I}<dl>
    <#list methodList as method>
${I}  <dt><span class="anchor" id="${method.symbol.htmlLink.htmlAnchor}">${method.symbol.name}:</span></dt>
${I}  <dd>
        <@doc_comments method.docComments, indent+2/>
${I}  </dd>
    </#list>
${I}</dl>
</#if>
<#if collaborationDiagramSvg??>

    <@collaboration_diagram symbol, collaborationDiagramSvg, indent/>
</#if>
