<#ftl output_format="HTML">
<#macro code_table_begin indent>
    <#local I>${""?left_pad(indent * 2)}</#local>
${I}<div class="code table-responsive"><div class="code-background"><table>
</#macro>

<#macro code_table_end indent>
    <#local I>${""?left_pad(indent * 2)}</#local>
${I}</table></div></div>
</#macro>
