<#ftl output_format="HTML">
<#include "doc_comment.inc.ftl">
<#include "symbol.inc.ftl">
<#include "collaboration_diagram.inc.ftl">

    <div class="msgdetail" id="${anchorName}">
<#if docComments.isDeprecated>
      <span class="deprecated">(deprecated) </span>
      <del><i>Service</i> ${name}</del>
<#else>
      <i>Service</i> ${name}
</#if>
    </div>
    <@doc_comments docComments 2 false/>

    <table>
      <tr><td class="docuCode">
        <table>
          <tr><td>service ${name}</td></tr>
          <tr><td>{</td></tr>
<#list methodList as method>
          <tr><td id="tabIndent">
            <@symbol_reference method.responseSymbol/> <#rt>
              <a href="#${method.anchorName}" class="fieldLink">${method.name}</a><#t>
              <#lt>(<@symbol_reference method.requestSymbol/>);
          </td></tr>
</#list>
          <tr><td>};</td></tr>
        </table>
      </td></tr>
    </table>
<#if methodList?has_content>

    <h3>Service methods</h3>

    <dl>
    <#list methodList as method>
      <dt class="memberItem"><a name="${method.anchorName}">${method.name}:</a></dt>
      <dd class="memberDetail">
        <@doc_comments method.docComments 4/>
      </dd>
    </#list>
    </dl>
</#if>
<#if collaborationDiagramSvgFileName??>

    <@collaboration_diagram collaborationDiagramSvgFileName/>
</#if>
