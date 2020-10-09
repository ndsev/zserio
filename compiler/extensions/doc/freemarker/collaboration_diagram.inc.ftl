<#ftl output_format="HTML">
<#macro collaboration_diagram svgFileName>
    <#if svgFileName??>
    <h3>Collaboration Diagram</h3>
    <object type="image/svg+xml" data="${svgFileName}">
        Collaboration diagram does not exist or SVG images are not supported by browser!
    </object>
    </#if>
</#macro>
