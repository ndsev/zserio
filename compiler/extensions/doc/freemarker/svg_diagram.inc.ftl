<#ftl output_format="HTML">
<#include "html_common.inc.ftl">
<#macro collaboration_diagram symbol svgFileName indent>
    <#local I>${""?left_pad(indent * 2)}</#local>
${I}<h3 class="anchor" id="${symbol.htmlLink.htmlAnchor}_collaboration">Collaboration Diagram</h3>
${I}<div class="svg">
${I}  <object type="image/svg+xml" data="<@html_path svgFileName/>">
${I}    Collaboration diagram does not exist or SVG images are not supported by browser!
${I}  </object>
${I}</div>
</#macro>
