<#-- converts native path to HTML path - i.e. convert backslashes to slashes ('\' -> '/') -->
<#macro html_path path>
    ${path?replace('\\', '/')}<#t>
</#macro>
