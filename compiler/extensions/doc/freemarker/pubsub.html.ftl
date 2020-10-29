<#ftl output_format="HTML">
<#include "doc_comment.inc.ftl">
<#include "symbol.inc.ftl">
<#include "svg_diagram.inc.ftl">

    <h2 class="anchor" id="${symbol.htmlLink.htmlAnchor}">
<#if docComments.isDeprecated>
      <span class="deprecated">(deprecated) </span>
      <del><i>Pubsub</i> ${symbol.name}</del>
<#else>
      <i>Pubsub</i> ${symbol.name}
</#if>
    </h2>
    <@doc_comments docComments 2, false/>

    <table>
      <tr><td class="docuCode">
        <table>
          <tr><td>pubsub ${symbol.name}</td></tr>
          <tr><td>{</td></tr>
<#list messageList as message>
          <tr><td class="tabIndent">
            ${message.keyword}(${message.topicDefinition}) <#rt>
              <@symbol_reference message.typeSymbol/> <@symbol_reference message.symbol/>;
          </td></tr>
</#list>
          <tr><td>};</td></tr>
        </table>
      </td></tr>
    </table>
<#if messageList?has_content>

    <h3>Pubsub messages</h3>

    <dl>
    <#list messageList as message>
      <dt class="memberItem"><a class="anchor" id="${message.symbol.htmlLink.htmlAnchor}">${message.symbol.name}:</a></dt>
      <dd class="memberDetail">
        <@doc_comments message.docComments, 4/>
      </dd>
    </#list>
    </dl>
</#if>
<#if collaborationDiagramSvg??>

    <@collaboration_diagram collaborationDiagramSvg/>
</#if>
