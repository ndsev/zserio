<#ftl output_format="HTML">
<#include "doc_comment.inc.ftl">
<#include "compound.inc.ftl">
<#include "usedby.inc.ftl">
<#include "collaboration_diagram.inc.ftl">
<#assign sqlTableHeading>
    <i>SQL Table<#if virtualTableUsing?has_content> VIRTUAL</#if></i> ${name}<#t>
      <#if virtualTableUsing?has_content> <i>USING</i> ${virtualTableUsing}</#if><#t>
</#assign>

    <div class="msgdetail" id="${anchorName}">
<#if docComments.isDeprecated>
      <span class="deprecated">(deprecated) </span>
      <del>${sqlTableHeading}</del>
<#else>
      ${sqlTableHeading}
</#if>
    </div>
    <@doc_comments docComments 2 false/>

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
    <@compound_member_details fields/>
    <@used_by usedByList/>
<#if collaborationDiagramSvg??>

    <@collaboration_diagram collaborationDiagramSvg/>
</#if>
