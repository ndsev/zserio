<#ftl output_format="HTML">
<#include "doc_comment.inc.ftl">
<#include "code.inc.ftl">
<#include "symbol.inc.ftl">
<#macro imports importNodes indent>
    <#local I>${""?left_pad(indent * 2)}</#local>
    <#if importNodes?has_content>
        <#list importNodes as importNode>
            <#if hasFloatingDocComments(importNode.docComments)>
                <#if !importNode?is_first>
${I}  </tbody>
    <@code_table_end indent/>
                </#if>
    <@doc_comments_floating importNode.docComments, indent/>
            </#if>
            <#if hasFloatingDocComments(importNode.docComments) || importNode?is_first>
    <@code_table_begin indent/>
${I}  <tbody>
            </#if>
<#if hasStickyDocComments(importNode.docComments)>
${I}    <tr class="doc"><td>
          <@doc_comments_sticky importNode.docComments, indent+3/>
${I}    </td></tr>
</#if>
${I}    <tr><td>
${I}      import <@symbol_reference importNode.importedPackageSymbol/>.<#rt>
            <#lt><#if importNode.importedSymbol??><@symbol_reference importNode.importedSymbol/><#else>*</#if>;
${I}    </td></tr>
        </#list>
${I}  </tbody>
    <@code_table_end indent/>
    </#if>
</#macro>
