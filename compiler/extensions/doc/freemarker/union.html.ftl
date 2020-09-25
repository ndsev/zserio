<#ftl output_format="HTML">
<#include "doc_comment.inc.ftl">
<#include "compound.inc.ftl">
<#include "linkedtype.inc.ftl">
<#include "usedby.inc.ftl">
<#include "collaboration_diagram.inc.ftl">

    <div class="msgdetail" id="${anchorName}">
<#if docComment.isDeprecated>
      <span class="deprecated">(deprecated) </span>
      <del>
</#if>
        <i>Union</i> ${name}
<#if docComment.isDeprecated>
      </del>
</#if>
    </div>

    <@doc_comment docComment false/>

    <table>
      <tr><td class="docuCode">
        <table>
          <tbody id="tabIndent">
            <tr><td colspan=3>union ${name}<@compound_parameters parameters/></td></tr>
            <tr><td colspan=3>{</td></tr>
            <@compound_fields anchorName fields/>
<#if functions?has_content>
            <tr><td colspan=3>&nbsp;</td></tr>
            <@compound_functions functions/>
</#if>
            <tr><td colspan=3>};</td></tr>
          </tbody>
        </table>
      </td></tr>
    </table>

    <h3>Member Details</h3>
    <@compound_member_details anchorName fields/>

    <@used_by usedByList/>
<#if collaborationDiagramSvgFileName??>

    <@collaboration_diagram collaborationDiagramSvgFileName/>
</#if>
