<#ftl output_format="HTML">
<#include "doc_comment.inc.ftl">
<#include "symbol.inc.ftl">
<#macro imports importNodes>
    <#if importNodes?has_content>
        <#list importNodes as importNode>
            <#if importNode.docComments.commentsList?has_content>
                <#if !importNode?is_first>
          <@import_end_table/>
                </#if>
          <@doc_comments importNode.docComments, 5, false/>
            </#if>
            <#if importNode.docComments.commentsList?has_content || importNode?is_first>
          <@import_begin_table/>
            </#if>
              <tr><td>
                import <@symbol_reference importNode.importedPackageSymbol/>.<#rt>
                  <#lt><#if importNode.importedSymbol??><@symbol_reference importNode.importedSymbol/><#else>*</#if>;
              </td></tr>
        </#list>
          <@import_end_table/>
    </#if>
</#macro>

<#macro import_begin_table>
          <div class="code">
          <table>
            <tbody>
</#macro>

<#macro import_end_table>
            </tbody>
          </table></div>
</#macro>