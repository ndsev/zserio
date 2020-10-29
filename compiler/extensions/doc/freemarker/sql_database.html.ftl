<#ftl output_format="HTML">
<#include "doc_comment.inc.ftl">
<#include "compound.inc.ftl">
<#include "usedby.inc.ftl">
<#include "svg_diagram.inc.ftl">

    <h2 class="anchor" id="${symbol.htmlLink.htmlAnchor}">
<#if docComments.isDeprecated>
      <span class="deprecated">(deprecated) </span>
      <del><i>SQL Database</i> ${symbol.name}</del>
<#else>
      <i>SQL Database</i> ${symbol.name}
</#if>
    </h2>
    <@doc_comments docComments 2, false/>

    <table>
      <tr><td class="docuCode">
        <table>
          <tbody>
            <tr><td colspan=3>sql_database ${symbol.name}</td></tr>
            <tr><td colspan=3>{</td></tr>
            <@compound_fields fields/>
            <tr><td colspan=3>};</td></tr>
          </tbody>
        </table>
      </td></tr>
    </table>
    <@compound_member_details fields/>
<#if collaborationDiagramSvg??>

    <@collaboration_diagram collaborationDiagramSvg/>
</#if>
