<#ftl output_format="HTML">
<#include "doc_comment.inc.ftl">
<#include "symbol.inc.ftl">
<#include "usedby.inc.ftl">
<#include "svg_diagram.inc.ftl">

    <h2 class="anchor" id="${symbol.htmlLink.htmlAnchor}">
<#if docComments.isDeprecated>
      <span class="deprecated">(deprecated) </span>
      <del><i>Constant</i> ${symbol.name}</del>
<#else>
      <i>Constant</i> ${symbol.name}
</#if>
    </h2>
    <@doc_comments docComments, 2, false/>

    <div class="code">
      const <@symbol_reference typeSymbol/> ${symbol.name} = ${value};
    </div>
    <@used_by usedByList/>
<#if collaborationDiagramSvg??>

    <@collaboration_diagram collaborationDiagramSvg/>
</#if>
