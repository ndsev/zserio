<#include "linkedtype.inc.ftl">

<#macro compound_fields fields>
    <#list fields as field>
        <#if field.alignmentExpression?has_content>
        <tr class="codeMember">
          <td></td>
          <td colspan=2>align(${field.alignmentExpression}):</td>
        </tr>
        </#if>
        <#if field.offsetExpression?has_content>
        <tr class="codeMember">
          <td></td><td colspan=2>${field.offsetExpression}</td>
        </tr>
        </#if>
        <tr class="codeMember">
          <td></td>
          <td valign="top" id="tabIndent"><@linkedtype field.linkedType/><#rt>
            <#lt><@compound_field_arguments field.arguments/></td>
          <td valign="bottom">
            <a href="#${field.name}" class="fieldLink">${field.name}</a><#rt>
                    ${field.arrayRange}<#t>
            <#if field.initializerExpression?has_content>
                    <#lt> = ${field.initializerExpression}<#rt>
            </#if>
            <#if field.optionalExpression?has_content>
                   <#lt> if ${field.optionalClauseExpression}<#rt>
            </#if>
            <#if field.constraintExpression?has_content>
                   <#lt> : ${field.constraintExpression}<#rt>
            </#if>
            ;<#t>
          </td>
            <#if field.sqlConstraintExpression?has_content>
          <td valign="bottom"><i>${field.sqlConstraintExpression}</i></td>
            </#if>
            <#if field.isVirtual>
          <td valign="bottom"><i>(virtual)</i></td>
            <#elseif field.isAutoOptional>
          <td valign="bottom"><i>(optional)</i></td>
            <#elseif field.isArrayImplicit>
          <td valign="bottom"><i>(implicit)</i></td>
            </#if>
          <td></td>
        </tr>
    </#list>
</#macro>

<#macro compound_functions functions>
    <#list functions as function>
        <tr>
          <td colspan=3 valign="top" id="tabIndent">function ${function.returnTypeName} ${function.name}()</td>
        </tr>
        <tr>
          <td colspan=3 id="tabIndent">{</td>
        </tr>
        <tr>
          <td></td>
          <td valign="top" id="tabIndent2">return</td>
          <td>${function.resultExpression};</td></tr>
        <tr><td colspan=3 id="tabIndent">}</td></tr>
    </#list>
</#macro>

<#macro compound_parameters parameters>
    <#if parameters?has_content>
  (<#t>
    <#list parameters as parameter>
      <@linkedtype parameter/> ${parameter.name}<#t>
      <#if parameter?has_next>, </#if><#t>
    </#list>
  )<#t>
    </#if>
</#macro>

<#macro compound_member_details fields>
    <#if fields?has_content>
    <dl>
        <#list fields as field>
      <dt class="memberItem">
        <a name="${field.name}">
            <#if field.docComment.isDeprecated>
          <span class="deprecated">(deprecated) </span>
          <del>
            </#if>
            ${field.name}
            <#if field.docComment.isDeprecated>
          </del>
            </#if>
          :
        </a>
      </dt>
      <dd class="memberDetail">
        <@doc_comment field.docComment/>
      </dd>
        </#list>
    </dl>
    </#if>
</#macro>

<#macro compound_field_arguments arguments>
    <#if arguments?has_content>
        (<#t>
        <#list arguments as argument>
            ${argument}<#if argument?has_next>, </#if><#t>
        </#list>
        )<#t>
    </#if>
</#macro>
