<#ftl output_format="HTML">
<#include "symbol.inc.ftl">
<#macro doc_comments docComments indent useCommentMarker=false>
    <#local I>${""?left_pad(indent * 2)}</#local>
    <#if docComments.commentsList?has_content>
        <#list docComments.commentsList as docComment>
    <@doc_comment docComment, indent, useCommentMarker/>
        </#list>
    </#if>
</#macro>

<#macro doc_comment doc indent useCommentMarker>
    <#local I>${""?left_pad(indent * 2)}</#local>
    <#if doc.paragraphs?size == 0>
        <#if doc.markdownHtml??>
    <@doc_markdown doc.markdownHtml, indent, useCommentMarker/>
        </#if>
    <#else>
${I}<div class="doc">
        <#if useCommentMarker>
${I}  /**
        </#if>
        <#list doc.paragraphs as paragraph>
            <#list paragraph.elements as element>
                <#if element.multiline??>
${I}  <div>
        <@doc_multiline_node element.multiline, indent+2/>
${I}  </div>
                </#if>
                <#if element.todoTag??>

${I}  <div>
${I}    <span>Todo:</span>
${I}    <@doc_multiline_node element.todoTag, indent+2/>
${I}  </div>
                </#if>
                <#if element.seeTag??>

${I}  <div>
${I}    <span>See:</span> <@symbol_reference element.seeTag.seeSymbol/>
${I}  </div>
                </#if>
                <#if element.paramTag??>

${I}  <div>
${I}    <span>Param: </span><code>${element.paramTag.name}</code>
        <@doc_multiline_node element.paramTag.description, indent+2/>
${I}  </div>
                </#if>
            </#list>
        </#list>
        <#if doc.isDeprecated>

${I}  <div>
${I}    <span>Note:</span> This element is deprecated and is going to be invalid in the future versions.
${I}  </div>
        </#if>
        <#if useCommentMarker>
${I}  */
        </#if>
${I}</div>
    </#if>
</#macro>

<#macro doc_button indent>
    <#local I>${""?left_pad(indent * 2)}</#local>
${I}<button class="btn shadow-none" onclick="toggleDoc(this);">
${I}  <svg class="bi" width="12" height="12"><use xlink:href="#chat-left-text"/></svg>
${I}</button>
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

<#macro doc_markdown markdownHtml indent useCommentMarker>
    <#local I>${""?left_pad(indent * 2)}</#local>
${I}<div class="doc">
    <#if useCommentMarker>
${I}  /*!
    </#if>
    <#list markdownHtml?split("\r?\n", "rm") as htmlLine>
${I}  ${htmlLine?no_esc}
    </#list>
    <#if useCommentMarker>
${I}  */
    </#if>
${I}</div>
</#macro>
