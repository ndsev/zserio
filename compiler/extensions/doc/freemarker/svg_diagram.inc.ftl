<#ftl output_format="HTML">
<#macro collaboration_diagram svgFileName indent>
    <#local I>${""?left_pad(indent * 2)}</#local>
${I}<h3>Collaboration Diagram</h3>
${I}<div class="svg">
${I}  <object type="image/svg+xml" data="${svgFileName}">
${I}    Collaboration diagram does not exist or SVG images are not supported by browser!
${I}  </object>
${I}</div>
</#macro>
