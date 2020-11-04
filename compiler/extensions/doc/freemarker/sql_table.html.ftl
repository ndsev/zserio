<#ftl output_format="HTML">
<#include "doc_comment.inc.ftl">
<#include "compound.inc.ftl">
<#include "usedby.inc.ftl">
<#include "svg_diagram.inc.ftl">
<#assign sqlTableHeading>
    <#if virtualTableUsing?has_content>virtual </#if><i>SQL Table</i><#t>
      <#if templateParameters?has_content> template</#if> ${symbol.name}<#t>
</#assign>

    <h2 class="anchor" id="${symbol.htmlLink.htmlAnchor}">
<#if docComments.isDeprecated>
      <span class="deprecated">(deprecated) </span>
      <del>${sqlTableHeading}</del>
<#else>
      ${sqlTableHeading}
</#if>
    </h2>
    <@doc_comments docComments 2, false/>

    <div class="code">
      <table>
        <tbody>
          <tr><td colspan=3>sql_table ${symbol.name}<@compound_template_parameters templateParameters/><#rt>
            <#lt><@compound_parameters parameters/><#rt>
            <#lt><#if virtualTableUsing?has_content> using ${virtualTableUsing}</#if></td></tr>
          <tr><td colspan=3>{</td></tr>
          <@compound_fields fields/>
<#if sqlConstraint?has_content>
          <tr><td colspan=3>&nbsp;</td></tr>
          <tr>
            <td class="indent"></td>
            <td colspan=2>sql ${sqlConstraint};</td>
          </tr>
</#if>
          <tr><td colspan=3>};</td></tr>
        </tbody>
      </table>
    </div>
    <@compound_member_details fields/>
    <@used_by usedByList/>
<#if collaborationDiagramSvg??>

    <@collaboration_diagram collaborationDiagramSvg/>
</#if>
