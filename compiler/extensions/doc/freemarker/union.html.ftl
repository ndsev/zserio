<#ftl output_format="HTML">
<#include "doc_comment.inc.ftl">
<#include "compound.inc.ftl">
<#include "usedby.inc.ftl">
<#include "collaboration_diagram.inc.ftl">

    <div class="msgdetail" id="${symbol.htmlLink.htmlAnchor}">
<#if docComments.isDeprecated>
      <span class="deprecated">(deprecated) </span>
      <del><i>Union</i> ${symbol.name}</del>
<#else>
      <i>Union</i> ${symbol.name}
</#if>
    </div>
    <@doc_comments docComments 2 false/>

    <table>
      <tr><td class="docuCode">
        <table>
          <tbody id="tabIndent">
            <tr><td colspan=3>union ${symbol.name}<@compound_parameters parameters/></td></tr>
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
