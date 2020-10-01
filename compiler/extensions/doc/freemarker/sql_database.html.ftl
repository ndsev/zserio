<#ftl output_format="HTML">
<#include "doc_comment.inc.ftl">
<#include "compound.inc.ftl">
<#include "usedby.inc.ftl">
<#include "collaboration_diagram.inc.ftl">

    <div class="msgdetail" id="${anchorName}">
<#if docComments.isDeprecated>
      <span class="deprecated">(deprecated) </span>
      <del>
</#if>
        <i>SQL Database</i> ${name}
<#if docComments.isDeprecated>
      </del>
</#if>
    </div>

    <@doc_comments docComments false/>

    <table>
      <tr><td class="docuCode">
        <table>
          <tbody id="tabIndent">
            <tr><td colspan=3>sql_database ${name}</td></tr>
            <tr><td colspan=3>{</td></tr>
            <@compound_fields fields/>
            <tr><td colspan=3>};</td></tr>
          </tbody>
        </table>
      </td></tr>
    </table>
    <@compound_member_details fields/>
<#if collaborationDiagramSvgFileName??>

    <@collaboration_diagram collaborationDiagramSvgFileName/>
</#if>
