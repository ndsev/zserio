<#ftl output_format="HTML">
<#include "symbol.inc.ftl">

<#macro compound_fields fields>
    <#list fields as field>
        <@compound_field field/>
    </#list>
</#macro>

<#macro compound_field field>
    <#if field.alignmentExpression?has_content>
            <tr class="codeMember">
              <td colspan=3>align(${field.alignmentExpression}):</td>
            </tr>
    </#if>
    <#if field.offsetExpression?has_content>
            <tr class="codeMember">
              <td colspan=3>${field.offsetExpression}:</td>
            </tr>
    </#if>
            <tr class="codeMember">
              <td id="tabIndent"></td>
              <td>
                <#if field.isVirtual>sql_virtual </#if><#t>
                <#if field.isAutoOptional>optional </#if><#t>
                <#if field.isArrayImplicit>implicit </#if><#t>
                  <#lt><@symbol_reference field.symbol/><@compound_field_arguments field.arguments/>
              </td>
              <td>
                <a href="#${field.anchorName}" class="fieldLink">${field.name}</a><#rt>
                  ${field.arrayRange}<#t>
    <#if field.initializerExpression?has_content>
                  <#lt> = ${field.initializerExpression}<#rt>
    </#if>
    <#if field.optionalClauseExpression?has_content>
                  <#lt> if ${field.optionalClauseExpression}<#rt>
    </#if>
    <#if field.constraintExpression?has_content>
                  <#lt> : ${field.constraintExpression}<#rt>
    </#if>
    <#if field.sqlConstraintExpression?has_content>
                  <#lt> sql ${field.sqlConstraintExpression}<#rt>
    </#if>
                  <#lt>;
              </td>
            </tr>
</#macro>

<#macro compound_functions functions>
    <#list functions as function>
            <tr>
              <td id="tabIndent"></td>
              <td colspan=2>
                function <@symbol_reference function.returnSymbol/> <#rt>
                  <#lt><a href="#${function.anchorName}" class="fieldLink">${function.name}()</a>
              </td>
            </tr>
            <tr>
              <td id="tabIndent"></td>
              <td colspan=2>{</td>
            </tr>
            <tr>
              <td id="tabIndent"></td>
              <td colspan=2 id="tabIndent">return ${function.resultExpression};</td>
            </tr>
            <tr>
              <td id="tabIndent"></td>
              <td colspan=2>}</td>
            </tr>
    </#list>
</#macro>

<#macro compound_parameters parameters>
    <#if parameters?has_content>
  (<#t>
    <#list parameters as parameter>
      <@symbol_reference parameter.symbol/> ${parameter.name}<#t>
      <#if parameter?has_next>, </#if><#t>
    </#list>
  )<#t>
    </#if>
</#macro>

<#macro compound_member_details fields>
    <#if fields?has_content>

    <h3>Member Details</h3>

    <dl>
        <#list fields as field>
      <dt class="memberItem">
        <a name="${field.anchorName}">
            <#if field.docComments.isDeprecated>
          <span class="deprecated">(deprecated) </span>
          <del>
            </#if>
            ${field.name}
            <#if field.docComments.isDeprecated>
          </del>
            </#if>
          :
        </a>
      </dt>
      <dd class="memberDetail">
        <@doc_comments field.docComments/>
      </dd>
        </#list>
    </dl>
    </#if>
</#macro>

<#macro compound_function_details functions>
    <#if functions?has_content>

    <h3>Function Details</h3>

    <dl>
        <#list functions as function>
      <@compound_function_detail function/>
        </#list>
    </dl>
    </#if>
</#macro>

<#macro compound_function_detail function>
      <dt class="memberItem">
        <a name="${function.anchorName}">
            <#if function.docComments.isDeprecated>
          <span class="deprecated">(deprecated) </span>
          <del>
            </#if>
            ${function.name}()
            <#if function.docComments.isDeprecated>
          </del>
            </#if>
          :
        </a>
      </dt>
      <dd class="memberDetail">
        <@doc_comments function.docComments/>
      </dd>
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
