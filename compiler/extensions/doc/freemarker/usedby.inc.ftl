<#ftl output_format="HTML">
<#include "symbol.inc.ftl">
<#macro used_by usedByList>
    <#if usedByList?has_content>

    <h3>Used By</h3>
    <div class="code">
      <table>
        <tbody>
        <#list usedByList as usedBySymbol>
          <tr><td><@symbol_reference usedBySymbol/></td></tr>
        </#list>
        </tbody>
      </table>
    </div>
    </#if>
</#macro>
