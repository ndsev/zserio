<#macro collaboration_diagram svgFileName>
    <#if svgFileName??>
    <h4>Collaboration Diagram</h4>
    <object type="image/svg+xml" data="${svgFileName}">
        Svg files not supported!
    </object>
    </#if>
</#macro>
