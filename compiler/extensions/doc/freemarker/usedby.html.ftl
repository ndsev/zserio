<#macro usedby containers>
    <h4>Used By</h4>
    <table>
    <tr><td class="docuCode">
      <table>
      <tbody id="tabIndent">
<#assign numOfContainers = containers?size>
<#if (numOfContainers > 0)>
    <#list containers as container>
        <#if container.linkedType??>
        <tr><td><@linkedtype container.linkedType/></td></tr>
        </#if>
    </#list>
<#else>
    <tr><td><div class="docuTag">&lt;<i>is not used by any CompoundType</i>&gt;</div></td></tr>
</#if>
      </tbody>
      </table>
    </td></tr>
    </table>
</#macro>
