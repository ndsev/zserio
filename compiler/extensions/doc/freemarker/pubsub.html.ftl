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
${I}  <del><i>Pubsub</i> ${symbol.name}</del>
<#else>
${I}  <i>Pubsub</i> ${symbol.name}
</#if>
${I}</h2>
    <@doc_comments docComments, indent, false/>

    <@code_table_begin indent/>
${I}  <tr><td>pubsub ${symbol.name}</td></tr>
${I}  <tr><td>{</td></tr>
<#list messageList as message>
${I}  <tr><td class="indent">
${I}    ${message.keyword}(${message.topicDefinition}) <#rt>
          <#lt><@symbol_reference message.typeSymbol/> <@symbol_reference message.symbol/>;
${I}  </td></tr>
</#list>
${I}  <tr><td>};</td></tr>
    <@code_table_end indent/>
<#if messageList?has_content>

${I}<h3 class="anchor" id="${symbol.htmlLink.htmlAnchor}_messages">Pubsub messages</h3>

${I}<dl>
    <#list messageList as message>
${I}  <dt><span class="anchor" id="${message.symbol.htmlLink.htmlAnchor}">${message.symbol.name}:</span></dt>
${I}  <dd>
        <@doc_comments message.docComments, indent+2/>
${I}  </dd>
    </#list>
${I}</dl>
</#if>
<#if collaborationDiagramSvg??>

    <@collaboration_diagram symbol, collaborationDiagramSvg, indent/>
</#if>
