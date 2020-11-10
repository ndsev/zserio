<#ftl output_format="HTML">
<#include "code.inc.ftl">
<#include "symbol.inc.ftl">
<#macro used_by usedBySymbols indent>
    <#local I>${""?left_pad(indent * 2)}</#local>
    <#if usedBySymbols?has_content>

${I}<h3>Used By</h3>
      <@code_table_begin indent+1/>
        <#list usedBySymbols as usedBySymbol>
${I}    <tr><td><@symbol_reference usedBySymbol/></td></tr>
        </#list>
      <@code_table_end indent+1/>
    </#if>
</#macro>
