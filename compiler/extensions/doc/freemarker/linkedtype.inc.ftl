<#macro linkedtype type>
  <#if !type.isBuiltIn>
    <#lt><a class  = "${type.style}"
       href   = "${type.packageName}.html#${type.hyperlinkName}"
       title  = "Type: ${type.category}"
       target = "detailedDocu">${type.name}</a><#rt>
  <#else>
    ${type.name}<#t>
  </#if>
</#macro>

<#macro fieldlinkedtype flt>
  <#assign linkedType = flt.linkedType>
  <#assign field      = flt.field>
  <#if !linkedType.isBuiltIn>
    <#lt><a  class   = "fieldLink"
        href    = "${linkedType.packageName}.html#{linkedType.hyperlinkName}"
        title   = "Type: ${linkedType.category}"
        target  = "detailedDocu">${field.name}</a><#rt>
  <#else>
    ${field.name}<#t>
  </#if>
</#macro>
