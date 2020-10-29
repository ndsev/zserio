<#ftl output_format="HTML">
<#include "doc_comment.inc.ftl">
<#include "compound.inc.ftl">
<#include "usedby.inc.ftl">
<#include "svg_diagram.inc.ftl">
<#assign unionHeading>
    <i>Union</i><#if templateParameters?has_content> template</#if> ${symbol.name}
</#assign>

    <h2 class="anchor" id="${symbol.htmlLink.htmlAnchor}">
<#if docComments.isDeprecated>
      <span class="deprecated">(deprecated) </span>
      <del>${unionHeading}</del>
<#else>
      ${unionHeading}
</#if>
    </h2>
    <@doc_comments docComments 2, false/>

    <table>
      <tr><td class="docuCode">
        <table>
          <tbody>
            <tr><td colspan=3>union ${symbol.name}<@compound_template_parameters templateParameters/><#rt>
              <#lt><@compound_parameters parameters/></td></tr>
            <tr><td colspan=3>{</td></tr>
            <@compound_fields fields/>
<#if functions?has_content>
            <tr><td colspan=3>&nbsp;</td></tr>
            <@compound_functions functions/>
</#if>
            <tr><td colspan=3>};</td></tr>
          </tbody>
        </table>
      </td></tr>
    </table>
    <@compound_member_details fields/>
    <@compound_function_details functions/>
    <@used_by usedByList/>
<#if collaborationDiagramSvg??>

    <@collaboration_diagram collaborationDiagramSvg/>
</#if>
