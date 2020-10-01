<#ftl output_format="HTML">
<#include "doc_comment.inc.ftl">
<#include "symbol.inc.ftl">
<#include "collaboration_diagram.inc.ftl">

    <div class="msgdetail" id="${anchorName}">
<#if docComments.isDeprecated>
      <span class="deprecated">(deprecated) </span>
      <del><i>Pubsub</i> ${name}</del>
<#else>
      <i>Pubsub</i> ${name}
</#if>
    </div>
    <@doc_comments docComments 2 false/>

    <table>
      <tr><td class="docuCode">
        <table>
          <tr><td>pubsub ${name}</td></tr>
          <tr><td>{</td></tr>
<#list messageList as message>
          <tr><td id="tabIndent">
            ${message.keyword}(${message.topicDefinition}) <#rt>
              <@symbol_reference message.symbol/> <#t>
              <#lt><a href="#${message.anchorName}" class="fieldLink">${message.name}</a>;
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
      <dt class="memberItem"><a name="${message.anchorName}">${message.name}:</a></dt>
      <dd class="memberDetail">
        <@doc_comments message.docComments 4/>
      </dd>
    </#list>
    </dl>
</#if>
<#if collaborationDiagramSvgFileName??>

    <@collaboration_diagram collaborationDiagramSvgFileName/>
</#if>
