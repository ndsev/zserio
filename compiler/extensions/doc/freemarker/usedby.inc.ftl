<#ftl output_format="HTML">
<#include "symbol.inc.ftl">
<#macro used_by symbols>
    <#if symbols?has_content>

    <h3>Used By</h3>
    <table>
      <tr><td class="docuCode">
        <table>
          <tbody id="tabIndent">
        <#list symbols as symbol>
            <tr><td><@symbol_reference symbol/></td></tr>
        </#list>
          </tbody>
        </table>
      </td></tr>
    </table>
    </#if>
</#macro>