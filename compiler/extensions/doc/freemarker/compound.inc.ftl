<#ftl output_format="HTML">
<#include "symbol.inc.ftl">
<#macro compound_fields fields indent>
    <#list fields as field>
        <@compound_field field, indent/>
    </#list>
</#macro>

<#macro compound_field field indent>
    <#local I>${""?left_pad(indent * 2)}</#local>
    <#local typePrefix>
        <#if field.isVirtual>sql_virtual </#if><#t>
        <#if field.isAutoOptional>optional </#if><#t>
        <#if field.isArrayImplicit>implicit </#if><#t>
    </#local>
    <#if field.alignmentExpression?has_content>
${I}<tr>
${I}  <td colspan=3>align(${field.alignmentExpression}):</td>
${I}</tr>
    </#if>
    <#if field.offsetExpression?has_content>
${I}<tr>
${I}  <td colspan=3>${field.offsetExpression}:</td>
${I}</tr>
    </#if>
${I}<tr>
${I}  <td class="indent empty"></td>
${I}  <td>
${I}    ${typePrefix}<@compound_field_type_name field/>
${I}  </td>
${I}  <td>
${I}    <@symbol_reference field.symbol/><#rt>
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
${I}  </td>
${I}</tr>
</#macro>

<#macro compound_functions functions indent>
    <#local I>${""?left_pad(indent * 2)}</#local>
    <#list functions as function>
${I}<tr>
${I}  <td class="indent empty"></td>
${I}  <td colspan=2>
${I}    function <@symbol_reference function.returnSymbol/> <#rt>
          <#lt><@symbol_reference function.symbol/>()</a>
${I}  </td>
${I}</tr>
${I}<tr>
${I}  <td class="indent empty"></td>
${I}  <td colspan=2>{</td>
${I}</tr>
${I}<tr>
${I}  <td class="indent empty"></td>
${I}  <td colspan=2 class="indent">return ${function.resultExpression};</td>
${I}</tr>
${I}<tr>
${I}  <td class="indent empty"></td>
${I}  <td colspan=2>}</td>
${I}</tr>
    </#list>
</#macro>

<#macro compound_template_parameters templateParameters>
    <#if templateParameters?has_content>
        &lt;<#t>
        <#list templateParameters as templateParameter>
            ${templateParameter.name}<#if templateParameter?has_next>, </#if><#t>
        </#list>
        &gt;<#t>
    </#if>
</#macro>

<#macro compound_parameters parameters>
    <#if parameters?has_content>
        (<#t>
        <#list parameters as parameter>
            <@symbol_reference parameter.symbol/> ${parameter.name}<#if parameter?has_next>, </#if><#t>
        </#list>
        )<#t>
    </#if>
</#macro>

<#macro compound_member_details symbol fields indent>
    <#local I>${""?left_pad(indent * 2)}</#local>
    <#if fields?has_content>

${I}<h3 class="anchor" id="${symbol.htmlLink.htmlAnchor}_member_details">Member Details</h3>

${I}<dl>
        <#list fields as field>
${I}  <dt>
${I}    <span class="anchor" id="${field.symbol.htmlLink.htmlAnchor}">
            <#if field.docComments.isDeprecated>
${I}      <span class="deprecated">(deprecated) </span>
${I}      <del>${field.symbol.name}</del>:
            <#else>
${I}      ${field.symbol.name}:
            </#if>
${I}    </span>
${I}  </dt>
${I}  <dd>
        <@doc_comments field.docComments, indent+2/>
${I}  </dd>
        </#list>
${I}</dl>
    </#if>
</#macro>

<#macro compound_function_details symbol functions indent>
    <#local I>${""?left_pad(indent * 2)}</#local>
    <#if functions?has_content>

${I}<h3 class="anchor" id="${symbol.htmlLink.htmlAnchor}_function_details">Function Details</h3>

${I}<dl>
        <#list functions as function>
      <@compound_function_detail function, indent+1/>
        </#list>
${I}</dl>
    </#if>
</#macro>

<#macro compound_function_detail function indent>
${I}<dt>
${I}  <span class="anchor" id="${function.symbol.htmlLink.htmlAnchor}">
    <#if function.docComments.isDeprecated>
${I}    <span class="deprecated">(deprecated) </span>
${I}    <del>${function.symbol.name}()</del>:
    <#else>
${I}    ${function.symbol.name}():
    </#if>
${I}  </span>
${I}</dt>
${I}<dd>
      <@doc_comments function.docComments, indent+1/>
${I}</dd>
</#macro>

<#macro compound_field_type_name field>
    <@symbol_reference field.typeSymbol/><@compound_field_type_arguments field.typeArguments/><#t>
    <#if field.dynamicBitFieldLengthExpression?has_content>
        &lt;${field.dynamicBitFieldLengthExpression}&gt;<#t>
    </#if>
</#macro>

<#macro compound_field_type_arguments typeArguments>
    <#if typeArguments?has_content>
        (<#t>
        <#list typeArguments as typeArgument>
            ${typeArgument}<#if typeArgument?has_next>, </#if><#t>
        </#list>
        )<#t>
    </#if>
</#macro>
