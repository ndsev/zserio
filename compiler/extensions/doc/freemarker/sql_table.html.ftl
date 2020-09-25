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
        <i>SQL Table<#if virtualTableUsing?has_content> VIRTUAL</#if></i> ${name}<#rt>
          <#lt><#if virtualTableUsing?has_content> <i>USING</i> ${virtualTableUsing}</#if>
<#if docComment.isDeprecated>
      </del>
</#if>
    </div>

    <@doc_comment docComment false/>

    <table>
      <tr><td class="docuCode">
        <table>
          <tbody id="tabIndent">
            <tr><td colspan=3>table ${name}<@compound_parameters parameters/></td></tr>
            <tr><td colspan=3>{</td></tr>
            <@compound_fields fields/>
<#if sqlConstraint?has_content>
            <tr><td colspan=3>&nbsp;</td></tr>
            <tr>
              <td id="tabIndent"></td>
              <td colspan=2>sql ${sqlConstraint};</td>
            </tr>
</#if>
            <tr><td colspan=3>};</td></tr>
          </tbody>
        </table>
      </td></tr>
    </table>

    <h2 class="msgdetail">Member Details</h2>
    <@compound_member_details fields/>

    <@used_by usedByList/>
<#if collaborationDiagramSvgFileName??>

    <@collaboration_diagram collaborationDiagramSvgFileName/>
</#if>
