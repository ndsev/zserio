<#ftl output_format="HTML">
<#include "doc_comment.inc.ftl">
<#include "linkedtype.inc.ftl">
<#include "collaboration_diagram.inc.ftl">

        <div class="msgdetail" id="${anchorName}">
<#if docComments.isDeprecated>
            <span class="deprecated">(deprecated) </span>
            <del>
</#if>
                <i>service</i> ${name}
<#if docComments.isDeprecated>
            </del>
</#if>
        </div>
        <p/>
        <@doc_comments docComments false/>

        <table>
        <tr><td class="docuCode">
            <table>
                <tr><td>service ${name}</td></tr>
                <tr><td>{</td></tr>
<#list methodList as method>
                <tr><td id="tabIndent">
                    <@linkedtype method.responseType/> <#rt>
                    <a href="#${method.anchorName}" class="fieldLink">${method.name}</a><#t>
                    <#lt>(<@linkedtype method.requestType/>);
                </td></tr>
</#list>
                <tr><td>};</td></tr>
            </table>
        </td></tr>
        </table>

        <h3>Service methods</h3>

        <dl>
<#list methodList as method>
            <dt class="memberItem"><a name="${method.anchorName}">${method.name}:</a></dt>
            <dd class="memberDetail">
            <@doc_comments method.docComments/>
            </dd>
</#list>
        </dl>
<#if collaborationDiagramSvgFileName??>

    <@collaboration_diagram collaborationDiagramSvgFileName/>
</#if>
