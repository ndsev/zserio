<#macro used_by linkedTypes>
    <#if linkedTypes?has_content>
    <h3>Used By</h3>
    <table>
      <tr><td class="docuCode">
        <table>
          <tbody id="tabIndent">
        <#list linkedTypes as linkedType>
            <tr><td><@linkedtype linkedType/></td></tr>
        </#list>
          </tbody>
        </table>
      </td></tr>
    </table>
    </#if>
</#macro>