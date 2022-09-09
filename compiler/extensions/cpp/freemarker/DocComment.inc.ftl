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
    <#if comment.isOneLiner && comment.paragraphs?size == 1 && comment.paragraphs[0].elements?size == 1>
${I}<#if !isInner>/**<#else> *</#if> <@doc_paragraph_element comment.paragraphs[0].elements[0], indent/><#if !isInner> */</#if>
    <#else>
<#if !isInner>
${I}/**
</#if>
        <#list comment.paragraphs as paragraph>
            <#if !paragraph?is_first>
${I} *
            </#if>
            <#list paragraph.elements as element>
${I} * <@doc_paragraph_element element, indent/>
            </#list>
        </#list>
<#if !isInner>
${I} */
</#if>
    </#if>
</#macro>

<#macro doc_paragraph_element paragraphElement indent>
    <#if paragraphElement.multiline??>
<@doc_multiline paragraphElement.multiline, indent, 0/><#rt>
    </#if>
    <#if paragraphElement.todoTag??>
\todo <@doc_multiline paragraphElement.todoTag, indent, 6/><#rt>
    </#if>
    <#if paragraphElement.seeTag??>
\see \ref ${paragraphElement.seeTag.link} "${paragraphElement.seeTag.alias}"<#rt>
    </#if>
    <#if paragraphElement.paramTag??>
\param ${paragraphElement.paramTag.name} <@doc_multiline paragraphElement.paramTag.description, indent, 7/><#rt>
    </#if>
    <#if paragraphElement.isDeprecated>
\deprecated<#rt>
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
