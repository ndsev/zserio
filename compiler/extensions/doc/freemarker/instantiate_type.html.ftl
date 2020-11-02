<#ftl output_format="HTML">
<#include "doc_comment.inc.ftl">
<#include "symbol.inc.ftl">
<#include "usedby.inc.ftl">
<#include "svg_diagram.inc.ftl">

    <h2 class="anchor" id="${symbol.htmlLink.htmlAnchor}">
<#if docComments.isDeprecated>
      <span class="deprecated">(deprecated) </span>
      <del><i>Instantiate Type</i> ${symbol.name}</del>
<#else>
      <i>Instantiate Type</i> ${symbol.name}
</#if>
    </h2>
    <p/>
    <@doc_comments docComments 2, false/>

    <div class="code">
      instantiate <@symbol_reference typeSymbol/> ${symbol.name};
    </div>
    <@used_by usedByList/>
<#if collaborationDiagramSvg??>

    <@collaboration_diagram collaborationDiagramSvg/>
</#if>
