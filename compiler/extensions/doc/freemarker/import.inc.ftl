<#ftl output_format="HTML">
<#include "doc_comment.inc.ftl">
<#include "symbol.inc.ftl">
<#macro imports importNodes>
    <#if importNodes?has_content>
          <div class="code">
          <table>
            <tbody>
        <#list importNodes as importNode>
              <tr><td>
                <@doc_comments importNode.docComments, 8, false/>
              </td></tr>
              <tr><td>
                import <@symbol_reference importNode.importedPackageSymbol/>.<#rt>
                  <#lt><#if importNode.importedSymbol??><@symbol_reference importNode.importedSymbol/><#else>*</#if>;
              </td></tr>
        </#list>
            </tbody>
          </table></div>
    </#if>
</#macro>
