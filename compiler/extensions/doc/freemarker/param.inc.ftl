<#macro parameterlist type>
  <#assign params = type.typeParameters>
  <#if (params?size > 0)>
    (<#t>
    <#list params as param>
      <@linkedtype toLinkedType(param.typeReference.type)/> ${param.name}<#t>
      <#if param_has_next>, </#if><#t>
    </#list>
    )<#t>
  </#if>
</#macro>

<#macro arglist field>
  <#if field.arguments??>
  <#assign args = field.arguments>
  <#compress>
    <#if (args?size > 0)>
      (<#t>
      <#list args as arg>
        ${emitExpression(arg)}<#if arg_has_next>, </#if><#t>
      </#list>
      )<#t>
    </#if>
  </#compress>
  </#if>
</#macro>
