<#macro doc_comments docComments>
    <#list docComments.comments as comment>
        <@doc_comment comment/>
    </#list>
</#macro>

<#macro doc_comment comment>
    <#if comment.isOneLiner && comment.paragraphs?size == 1 && comment.paragraphs[0].elements?size == 1>
/** <@doc_paragraph_element comment.paragraphs[0].elements[0]/> */
    <#else>
/**
    <#list comment.paragraphs as paragraph>
        <#if !paragraph?is_first>
 *
        </#if>
        <#list paragraph.elements as element>
 * <@doc_paragraph_element element/>
        </#list>
    </#list>
 */
    </#if>
</#macro>

<#macro doc_paragraph_element paragraphElement>
    <#if paragraphElement.multiline??>
<@doc_multiline paragraphElement.multiline, 0/><#rt>
    </#if>
    <#if paragraphElement.todoTag??>
\todo <@doc_multiline paragraphElement.todoTag, 6/><#rt>
    </#if>
    <#if paragraphElement.seeTag??>
\see \ref ${paragraphElement.seeTag.link} "${paragraphElement.seeTag.alias}"<#rt>
    </#if>
    <#if paragraphElement.paramTag??>
\param ${paragraphElement.paramTag.name} <@doc_multiline paragraphElement.paramTag.description, 7/><#rt>
    </#if>
    <#if paragraphElement.isDeprecated>
\deprecated<#rt>
    </#if>
</#macro>

<#macro doc_multiline multiline spaceIndent>
    <#local I>${""?left_pad(spaceIndent)}</#local>
    <#list multiline.lines as line>
        <#if !line?is_first>
 * ${I}<#rt>
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
