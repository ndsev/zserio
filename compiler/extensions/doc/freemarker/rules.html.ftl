<#ftl output_format="HTML">
<#include "doc_comment.inc.ftl">
<#include "symbol.inc.ftl">
<#assign indent = 5>
<#assign I>${""?left_pad(indent * 2)}</#assign>

<#if hasFloatingDocComments(docComments)>
    <@doc_comments_floating docComments, indent/>

</#if>
${I}<h2 class="anchor" id="${symbol.htmlLink.htmlAnchor}">
${I}  <span<#if docComments.isDeprecated> class="deprecated"</#if>>Rules ${symbol.name}</span>
${I}</h2>

<#if hasStickyDocComments(docComments)>
    <@doc_comments_sticky docComments, indent, false/>
</#if>

${I}<div class="rules table table-responsive"><table><tbody>
<#list rules as rule>
${I}  <tr>
${I}    <th scope="row">
${I}      <span class="anchor-group" id="${rule.symbol.htmlLink.htmlAnchor}"><@symbol_reference rule.symbol/></span>
${I}    </th>
${I}    <td>
          <@doc_comments_all rule.docComments, indent+3, false/>
        </td>
${I}  </tr>
</#list>
${I}</tbody></table></div>