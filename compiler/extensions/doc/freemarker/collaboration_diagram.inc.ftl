<#ftl output_format="HTML">
<#macro collaboration_diagram svgFileName>
    <#if svgFileName??>
    <h3>Collaboration Diagram</h3>
    <object type="image/svg+xml" data="${svgFileName}">
        Svg files not supported!
    </object>
    </#if>
</#macro>
