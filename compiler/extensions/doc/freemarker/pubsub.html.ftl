<#ftl output_format="HTML">
<#include "doc_comment.inc.ftl">
<#include "linkedtype.inc.ftl">
<#include "collaboration_diagram.inc.ftl">

        <div class="msgdetail" id="${linkedType.hyperlinkName}">
<#if docComment.isDeprecated>
            <span class="deprecated">(deprecated) </span>
            <del>
</#if>
                <i>pubsub</i> ${name}
<#if docComment.isDeprecated>
            </del>
</#if>
        </div>
        <p/>
        <@doc_comment docComment false/>

        <table>
        <tr><td class="docuCode">
            <table>
                <tr><td>pubsub ${name}</td></tr>
                <tr><td>{</td></tr>
<#list messageList as message>
                <tr><td id="tabIndent">
                    ${message.keyword}(${message.topicDefinition}) <#rt>
                    <@linkedtype message.type/> <#rt>
                    <#lt><a href="#${linkedType.hyperlinkName}_${message.name}" class="fieldLink">${message.name}</a>;
                </td></tr>
</#list>
                <tr><td>};</td></tr>
            </table>
        </td></tr>
        </table>

        <h3>Pubsub messages</h3>

        <dl>
<#list messageList as message>
            <dt class="memberItem"><a name="${linkedType.hyperlinkName}_${message.name}">${message.name}:</a></dt>
            <dd class="memberDetail">
            <@doc_comment message.docComment/>
            </dd>
</#list>
        </dl>
<#if collaborationDiagramSvgFileName??>

    <@collaboration_diagram collaborationDiagramSvgFileName/>
</#if>
