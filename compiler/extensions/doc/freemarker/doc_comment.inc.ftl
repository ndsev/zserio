<#ftl output_format="HTML">
<#include "symbol.inc.ftl">
<#function hasDocComments docComments>
    <#return hasFloatingDocComments(docComments) || hasStickyDocComments(docComments)>
</#function>

<#function hasFloatingDocComments docComments>
    <#return docComments.floatingCommentsList?has_content>
</#function>

<#function hasStickyDocComments docComments>
    <#return docComments.stickyCommentsList?has_content>
</#function>

<#macro doc_comments_all docComments indent isCode=true>
    <@doc_comments_floating docComments, indent, isCode/>
    <@doc_comments_sticky docComments, indent, isCode/>
</#macro>

<#macro doc_comments_floating docComments indent isCode=false>
    <#if docComments.floatingCommentsList?has_content>
        <#list docComments.floatingCommentsList as floatingDocComment>
    <@doc_comment floatingDocComment, indent, isCode/>
        </#list>
    </#if>
</#macro>

<#macro doc_comments_sticky docComments indent isCode=true>
    <#if docComments.stickyCommentsList?has_content>
        <#list docComments.stickyCommentsList as stickyDocComment>
    <@doc_comment stickyDocComment, indent, isCode/>
        </#list>
    </#if>
</#macro>

<#macro doc_comment doc indent isCode>
    <#local I>${""?left_pad(indent * 2)}</#local>
    <#local tagName=doc.isOneLiner?then("span","div")/>
    <#if doc.paragraphs?size == 0>
        <#if doc.markdownHtml??>
    <@doc_markdown doc, indent, isCode/>
        </#if>
    <#else>
${I}<div class="doc">
        <#if isCode>
${I}  /**
        </#if>
        <#list doc.paragraphs as paragraph>
            <#list paragraph.elements as element>
                <#if element.multiline??>
${I}  <${tagName}>
        <@doc_multiline_node element.multiline, indent+2/>
${I}  </${tagName}>
                </#if>
                <#if element.todoTag??>

${I}  <${tagName}>
${I}    <span>Todo:</span>
${I}    <@doc_multiline_node element.todoTag, indent+2/>
${I}  </${tagName}>
                </#if>
                <#if element.seeTag??>
${I}  <${tagName}>
${I}    <span>See:</span> <@symbol_reference element.seeTag.seeSymbol/>
${I}  </${tagName}>
                </#if>
                <#if element.paramTag??>

${I}  <${tagName}>
${I}    <span>Param: </span><code>${element.paramTag.name}</code>
        <@doc_multiline_node element.paramTag.description, indent+2/>
${I}  </${tagName}>
                </#if>
            </#list>
        </#list>
        <#if doc.isDeprecated>

${I}  <${tagName}>
${I}    <span>Note:</span> This element is deprecated and is going to be invalid in the future versions.
${I}  </${tagName}>
        </#if>
        <#if isCode>
${I}  */
        </#if>
${I}</div>
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

<#function getMarkdownHtml doc, isCode>
    <#if isCode>
        <#if doc.isOneLiner>
            <#local markedMarkdownHtml=doc.markdownHtml?replace("^(<[^>]*>)", "$1/*! ", "r")>
            <#local markedMarkdownHtml=markedMarkdownHtml?replace("(<[^>]*>)$", " */$1", "r")>
            <#return markedMarkdownHtml>
        <#else>
            <#return "/*! ${doc.markdownHtml} */">
        </#if>
    <#else>
        <#return doc.markdownHtml>
    </#if>
</#function>

<#macro doc_markdown doc indent isCode>
    <#local I>${""?left_pad(indent * 2)}</#local>
${I}<div class="doc">
    <#list getMarkdownHtml(doc, isCode)?split("\r?\n", "rm") as htmlLine>
${I}  ${htmlLine?no_esc}
    </#list>
${I}</div>
</#macro>

<#macro doc_button indent>
    <#local I>${""?left_pad(indent * 2)}</#local>
${I}<button class="btn shadow-none" onclick="toggleDoc(this);">
${I}  <svg class="bi" width="12" height="12"><use xlink:href="#chat-left"/></svg>
${I}</button>
</#macro>
