<#include "linkedtype.inc.ftl">

<#macro compound_fields fields numCols=3>
    <#list fields as field>
        <@compound_field field numCols/>
    </#list>
</#macro>

<#macro compound_field field numCols=3>
    <#if field.alignmentExpression?has_content>
        <tr class="codeMember">
          <td colspan=${numCols}>align(${field.alignmentExpression}):</td>
        </tr>
    </#if>
    <#if field.offsetExpression?has_content>
        <tr class="codeMember">
          <td colspan=${numCols}>${field.offsetExpression}:</td>
        </tr>
    </#if>
        <tr class="codeMember">
          <td colspan=${numCols-2} id="tabIndent"></td>
          <td>
            <#if field.isVirtual>virtual </#if><#t>
            <#if field.isAutoOptional>optional </#if><#t>
            <#if field.isArrayImplicit>implicit </#if><#t>
            <#lt><@linkedtype field.linkedType/><@compound_field_arguments field.arguments/>
          </td>
          <td>
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
    <#if field.sqlConstraintExpression?has_content>
              <#lt> ${field.sqlConstraintExpression}<#rt>
    </#if>
              ;<#t>
          </td>
        </tr>
</#macro>

<#macro compound_functions functions numCols=3>
    <#list functions as function>
        <tr>
          <td id="tabIndent"></td>
          <td colspan=${numCols-1}>function <@linkedtype function.returnType/> ${function.name}()</td>
        </tr>
        <tr>
          <td id="tabIndent"></td>
          <td colspan=${numCols-1}>{</td>
        </tr>
        <tr>
          <td id="tabIndent"></td>
          <td colspan=${numCols-1} id="tabIndent">return ${function.resultExpression};</td>
        </tr>
        <tr>
          <td id="tabIndent"></td>
          <td colspan=${numCols-1}>}</td>
        </tr>
    </#list>
</#macro>

<#macro compound_parameters parameters>
    <#if parameters?has_content>
  (<#t>
    <#list parameters as parameter>
      <@linkedtype parameter.linkedType/> ${parameter.name}<#t>
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
