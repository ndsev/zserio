
<#macro linkedtype type>
  <#if !type.isBuiltIn>
<a  class  = "referenceLink"
    href   = "content/${type.packageName}/${type.hyperlinkName}.html"
    title  = "Type: ${type.category}"
    target = "detailedDocu" >${type.name}</a>
  <#else>
    ${type.name}<#t>
  </#if>
</#macro>

<#macro fieldlinkedtype flt>
  <#assign linkedType = flt.linkedType>
  <#assign field      = flt.field>
  <#if !linkedType.isBuiltIn>
<a  class   = "referenceLink"
    href    = "content/${linkedType.packageName}/${linkedType.hyperlinkName}.html"
    title   = "Type: ${linkedType.category}"
    target  = "detailedDocu" >${field.name}</a>
  <#else>
    ${field.name}<#t>
  </#if>
</#macro>

<html>
  <head>
    <title>Deprecated elements</title>
    <link rel="stylesheet" type="text/css" href="webStyles.css">
    <meta http-equiv="content-type" content="text/html; charset=UTF-8">
  </head>
  <body>

    <h2>Deprecated elements</h2>
    <div class="docuTag">The following zserio elements are deprecated. Their support is about to get discontinued.</div>
    <ul class="deprecatedList">
  <#if items?has_content>
    <#list items as item>
      <#if item.isField>
        <li><span class="deprecatedDetail">field</span> ${item.fieldOwner.package.packageName}.<@linkedtype item.fieldCompoundLinkedType/>.<@fieldlinkedtype item.fieldLinkedType/></li>
      <#elseif item.isEnumItem>
        <li><span class="deprecatedDetail">enum-item</span> ${item.enumType.package.packageName}.<@linkedtype item.enumLinkedType/>.${item.enumItem.name}</li>
      <#elseif item.isBitmaskValue>
        <li><span class="deprecatedDetail">bitmask-value</span> ${item.bitmaskType.package.packageName}.<@linkedtype item.bitmaskLinkedType/>.${item.bitmaskValue.name}</li>
      <#else>
        <li><span class="deprecatedDetail">type</span> ${item.packageName}.<@linkedtype item.linkedType/></li>
      </#if>
    </#list>
  <#else>
    <li>&#60;no elements are indicated to be deprecated.&#62;</li>
  </#if>
    </ul>

  </body>
</html>



