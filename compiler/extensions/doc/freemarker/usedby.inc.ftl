<#macro used_by linkedTypes>
    <#if linkedTypes?has_content>
    <h4>Used By</h4>
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