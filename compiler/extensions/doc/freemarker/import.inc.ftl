<#ftl output_format="HTML">
<#include "doc_comment.inc.ftl">
<#include "code.inc.ftl">
<#include "symbol.inc.ftl">
<#macro imports importNodes indent>
    <#local I>${""?left_pad(indent * 2)}</#local>
    <#if importNodes?has_content>
        <#list importNodes as importNode>
            <#if importNode.docComments.commentsList?has_content>
                <#if !importNode?is_first>
    <@code_table_end indent/>
                </#if>
    <@doc_comments importNode.docComments, indent/>
            </#if>
            <#if importNode.docComments.commentsList?has_content || importNode?is_first>
    <@code_table_begin indent/>
            </#if>
${I}  <tr><td>
${I}    import <@symbol_reference importNode.importedPackageSymbol/>.<#rt>
          <#lt><#if importNode.importedSymbol??><@symbol_reference importNode.importedSymbol/><#else>*</#if>;
${I}  </td></tr>
        </#list>
    <@code_table_end indent/>
    </#if>
</#macro>
