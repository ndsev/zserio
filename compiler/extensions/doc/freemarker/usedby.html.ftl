<#macro usedby containers services>
    <#assign numOfUses = containers?size + services?size>
    <#if (numOfUses > 0)>
    <h4>Used By</h4>
    <table>
    <tr><td class="docuCode">
      <table>
      <tbody id="tabIndent">
    <#list containers as container>
        <#if container.linkedType??>
        <tr><td><@linkedtype container.linkedType/></td></tr>
        </#if>
    </#list>
    <#list services as service>
        <tr><td><@linkedtype service/></td></tr>
    </#list>
      </tbody>
      </table>
    </td></tr>
    </table>
    </#if>
</#macro>
