<#include "doc_comment.html.ftl">
<#include "linkedtype.html.ftl">
<#include "collaboration_diagram.html.ftl">
<html>
    <head>
        <title>pubsub ${packageName}.${name}</title>
        <link rel="stylesheet" type="text/css" href="../../webStyles.css">
        <meta http-equiv="content-type" content="text/html; charset=UTF-8">
    </head>

    <body>
        <h2>${packageName}</h2>
        <div class="msgdetail">
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
        <@doc_comment docComment/>

        <table>
        <tr><td class="docuCode">
            <table>
                <tr><td>pubsub ${name}</td></tr>
                <tr><td>{</td></tr>
<#list messageList as message>
                <tr><td id="tabIndent">
                    ${message.keyword}("${message.topicDefinition}") <#rt>
                    <@linkedtype message.type/> <#rt>
                    <#lt><a href="#${message.name}" class="fieldLink">${message.name}</a>;
                </td></tr>
</#list>
                <tr><td>};</td></tr>
            </table>
        </td></tr>
        </table>

        <h2>Pubsub messages</h2>

        <dl>
<#list messageList as message>
            <dt class="memberItem"><a name="${message.name}">${message.name}:</a></dt>
            <dd class="memberDetail">
            <@doc_comment message.docComment/>
            </dd>
</#list>
        </dl>
<#if collaborationDiagramSvgFileName??>

    <@collaboration_diagram collaborationDiagramSvgFileName/>
</#if>
    </body>
</html>
