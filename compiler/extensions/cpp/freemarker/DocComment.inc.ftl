<#macro doc_comments docComments indent=0>
    <#local I>${""?left_pad(indent * 4)}</#local>
${I}/**
    <@doc_comment docComments, indent/>
${I} */
</#macro>

<#macro doc_comments_inner docComments indent=0>
    <@doc_comment docComments, indent/>
</#macro>

<#macro doc_comment comment indent>
    <#local I>${""?left_pad(indent * 4)}</#local>
    <#list comment.paragraphs as paragraph>
        <#if !paragraph?is_first && (has_paragraph_multiline(paragraph) || has_paragraph_todo_tag(paragraph) ||
                has_paragraph_see_tag(paragraph))>
${I} *
        </#if>
        <#list paragraph.elements as paragraphElement>
            <#if paragraphElement.multiline??>
${I} * <@doc_multiline paragraphElement.multiline, indent, 0/>
            </#if>
            <#if paragraphElement.todoTag??>
${I} * \todo <@doc_multiline paragraphElement.todoTag, indent, 6/>
            </#if>
            <#if paragraphElement.seeTag??>
${I} * \see <span/> \ref ${paragraphElement.seeTag.link} "${paragraphElement.seeTag.alias}"
            </#if>
        </#list>
    </#list>
    <#if has_comment_param_tag(comment)>
${I} *
${I} * \b Parameters:
        <#list comment.paragraphs as paragraph>
            <#list paragraph.elements as paragraphElement>
                <#if paragraphElement.paramTag??>
${I} * - \b ${paragraphElement.paramTag.name} <@doc_multiline paragraphElement.paramTag.description, indent, 2/>
                </#if>
            </#list>
        </#list>
    </#if>
    <#if has_comment_deprecated_tag(comment)>
        <#-- Keyword '@deprecated' is not used because it fires warning in clang. -->
${I} *
${I} * \b Deprecated
    </#if>
</#macro>

<#macro doc_multiline multiline indent multiLineIndent>
    <#local I>${""?left_pad(indent * 4)}</#local>
    <#local M>${""?left_pad(multiLineIndent)}</#local>
    <#list multiline.lines as line>
        <#if !line?is_first>
${I} * ${M}<#rt>
        </#if>
        <#list line.lineElements as lineElement>
            <#if lineElement.docString??>
<#if !lineElement?is_first> </#if>${lineElement.docString}<#rt>
            </#if>
            <#if lineElement.seeTag??>
<#if !lineElement?is_first> </#if>\ref ${lineElement.seeTag.link} "${lineElement.seeTag.alias}"<#rt>
            </#if>
        </#list>
        <#if !line?is_last>

        </#if>
    </#list>
</#macro>

<#function has_paragraph_multiline paragraph>
    <#list paragraph.elements as paragraphElement>
        <#if paragraphElement.multiline??>
            <#return true>
        </#if>
    </#list>
    <#return false>
</#function>

<#function has_paragraph_todo_tag paragraph>
    <#list paragraph.elements as paragraphElement>
        <#if paragraphElement.todoTag??>
            <#return true>
        </#if>
    </#list>
    <#return false>
</#function>

<#function has_paragraph_see_tag paragraph>
    <#list paragraph.elements as paragraphElement>
        <#if paragraphElement.seeTag??>
            <#return true>
        </#if>
    </#list>
    <#return false>
</#function>

<#function has_comment_param_tag comment>
    <#list comment.paragraphs as paragraph>
        <#list paragraph.elements as paragraphElement>
            <#if paragraphElement.paramTag??>
                <#return true>
            </#if>
        </#list>
    </#list>
    <#return false>
</#function>

<#function has_comment_deprecated_tag comment>
    <#list comment.paragraphs as paragraph>
        <#list paragraph.elements as paragraphElement>
            <#if paragraphElement.isDeprecated>
                <#return true>
            </#if>
        </#list>
    </#list>
    <#return false>
</#function>
