<#include "doc_comment.html.ftl">
<#include "linkedtype.html.ftl">
<#include "collaboration_diagram.html.ftl">

        <div class="msgdetail" id="${linkedType.hyperlinkName}">
<#if docComment.isDeprecated>
            <span class="deprecated">(deprecated) </span>
            <del>
</#if>
                <i>service</i> ${name}
<#if docComment.isDeprecated>
            </del>
</#if>
        </div>
        <p/>
        <@doc_comment docComment/>

        <table>
        <tr><td class="docuCode">
            <table>
                <tr><td>service ${name}</td></tr>
                <tr><td>{</td></tr>
<#list methodList as method>
                <tr><td id="tabIndent">
                    <@linkedtype method.responseType/> <#rt>
                    <a href="#${method.name}" class="fieldLink">${method.name}</a><#t>
                    <#lt>(<@linkedtype method.requestType/>);
                </td></tr>
</#list>
                <tr><td>};</td></tr>
            </table>
        </td></tr>
        </table>

        <h2>Service methods</h2>

        <dl>
<#list methodList as method>
            <dt class="memberItem"><a name="${method.name}">${method.name}:</a></dt>
            <dd class="memberDetail">
            <@doc_comment method.docComment/>
            </dd>
</#list>
        </dl>
<#if collaborationDiagramSvgFileName??>

    <@collaboration_diagram collaborationDiagramSvgFileName/>
</#if>
