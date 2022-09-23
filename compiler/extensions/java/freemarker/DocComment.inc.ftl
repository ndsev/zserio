<#macro doc_comments docComments indent=0>
    <#list docComments.comments as comment>
        <@doc_comment comment, false, indent/>
    </#list>
</#macro>

<#macro doc_comments_inner docComments indent=0>
    <#list docComments.comments as comment>
        <@doc_comment comment, true, indent/>
    </#list>
</#macro>

<#macro doc_comment comment isInner indent>
    <#local I>${""?left_pad(indent * 4)}</#local>
    <#if comment.isOneLiner && comment.paragraphs?size == 1 && comment.paragraphs[0].elements?size == 1 &&
            comment.paragraphs[0].elements[0].multiline??>
${I}<#if !isInner>/**<#else> *</#if> <@doc_multiline comment.paragraphs[0].elements[0].multiline, indent, 0/><#if !isInner> */</#if>
    <#else>
        <#if !isInner>
${I}/**
        </#if>
        <#local isFirstParagraph=true>
        <#list comment.paragraphs as paragraph>
            <#if has_paragraph_multiline(paragraph)>
                <#if isFirstParagraph>
                    <#local isFirstParagraph=false>
                <#else>
${I} * <p>
                </#if>
                <#list paragraph.elements as element>
                    <#if element.multiline??>
${I} * <@doc_multiline element.multiline, indent, 0/>
                    </#if>
                </#list>
            </#if>
        </#list>
        <#if has_comment_todo_tag(comment)>
${I} * <p>
${I} * <b>Todo:</b>
            <#list comment.paragraphs as paragraph>
                <#list paragraph.elements as element>
                    <#if element.todoTag??>
${I} * <br><@doc_multiline element.todoTag, indent, 0/>
                    </#if>
                </#list>
            </#list>
        </#if>
        <#if has_comment_param_tag(comment)>
${I} * <p>
${I} * <b>Parameters:</b>
            <#list comment.paragraphs as paragraph>
                <#list paragraph.elements as element>
                    <#if element.paramTag??>
${I} * <br>${element.paramTag.name} - <@doc_multiline element.paramTag.description, indent, element.paramTag.name?length + 7/>
                    </#if>
                </#list>
            </#list>
        </#if>
        <#if has_comment_see_tag(comment)>
${I} *
            <#list comment.paragraphs as paragraph>
                <#list paragraph.elements as element>
                    <#if element.seeTag??>
${I} * @see ${element.seeTag.link} ${element.seeTag.alias}
                    </#if>
                </#list>
            </#list>
        </#if>
        <#if has_comment_deprecated_tag(comment)>
${I} *
${I} * @deprecated
        </#if>
        <#if !isInner>
${I} */
        </#if>
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
<#if !lineElement?is_first> </#if>{@link ${lineElement.seeTag.link} ${lineElement.seeTag.alias}}<#rt>
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

<#function has_comment_todo_tag comment>
    <#list comment.paragraphs as paragraph>
        <#list paragraph.elements as paragraphElement>
            <#if paragraphElement.todoTag??>
                <#return true>
            </#if>
        </#list>
    </#list>
    <#return false>
</#function>

<#function has_comment_see_tag comment>
    <#list comment.paragraphs as paragraph>
        <#list paragraph.elements as paragraphElement>
            <#if paragraphElement.seeTag??>
                <#return true>
            </#if>
        </#list>
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
