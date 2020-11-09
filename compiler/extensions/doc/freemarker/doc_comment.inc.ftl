<#ftl output_format="HTML">
<#include "symbol.inc.ftl">
<#macro doc_comments docComments indent useNoDocPlaceholder=true>
    <#local I>${""?left_pad(indent * 2)}</#local>
    <#if docComments.commentsList?has_content>
        <#list docComments.commentsList as docComment>
    <@doc_comment docComment, indent/>
        </#list>
    <#elseif useNoDocPlaceholder>
${I}<div class="doc">&lt;<i>no documentation found</i>&gt;</div>
    </#if>
</#macro>

<#macro doc_comment doc indent>
    <#local I>${""?left_pad(indent * 2)}</#local>
    <#if doc.paragraphs?size == 0>
        <#if doc.markdownHtml??>
    <@doc_markdown doc.markdownHtml, indent/>
        </#if>
    <#else>
        <#list doc.paragraphs as paragraph>
            <#list paragraph.elements as element>
                <#if element.multiline??>
${I}<div class="doc">
      <@doc_multiline_node element.multiline, indent+1/>
${I}</div>
                </#if>
                <#if element.todoTag??>

${I}<div class="doc">
${I}  <span>Todo:</span>
${I}  <@doc_multiline_node element.todoTag, indent+1/>
${I}</div>
                </#if>
                <#if element.seeTag??>

${I}<div class="doc">
${I}  <span>See:</span> <@symbol_reference element.seeTag.seeSymbol/>
${I}</div>
                </#if>
                <#if element.paramTag??>

${I}<div class="doc">
${I}  <span>Param: </span><code>${element.paramTag.name}</code>
      <@doc_multiline_node element.paramTag.description, indent+1/>
${I}</div>
                </#if>
            </#list>
        </#list>
        <#if doc.isDeprecated>

${I}<div class="doc">
${I}  <span>Note:</span> This element is deprecated and is going to be invalid in the future versions.
${I}</div>
        </#if>
    </#if>
</#macro>

<#macro doc_multiline_node multilineNode indent>
    <#local I>${""?left_pad(indent * 2)}</#local>
    <#list multilineNode.docLineElements as docLineElement>
        <#if docLineElement.docString??>
${I}${docLineElement.docString?no_esc}
        </#if>
        <#if docLineElement.seeTag??>
${I}<@symbol_reference docLineElement.seeTag.seeSymbol/>
        </#if>
    </#list>
</#macro>

<#macro doc_markdown markdownHtml indent>
    <#local I>${""?left_pad(indent * 2)}</#local>
${I}<div class="doc">
    <#list markdownHtml?split("\r?\n", "rm") as htmlLine>
${I}  ${htmlLine?no_esc}
    </#list>
${I}</div>
</#macro>
