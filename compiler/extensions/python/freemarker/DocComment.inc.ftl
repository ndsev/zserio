<#macro doc_comments docComments indent=0>
    <#local I>${""?left_pad(indent * 4)}</#local>
${I}"""
    <@doc_comment docComments, indent/>
${I}"""
</#macro>

<#macro doc_comments_inner docComments indent=0>
    <@doc_comment docComments, indent/>
</#macro>

<#macro doc_comment comment indent>
    <#local I>${""?left_pad(indent * 4)}</#local>
        <#list comment.paragraphs as paragraph>
            <#if !paragraph?is_first>

            </#if>
            <#list paragraph.elements as element>
${I}<@doc_paragraph_element element, indent/>
            </#list>
        </#list>
</#macro>

<#macro doc_paragraph_element paragraphElement indent>
    <#if paragraphElement.multiline??>
<@doc_multiline paragraphElement.multiline, indent, 0/><#rt>
    </#if>
    <#if paragraphElement.todoTag??>
.. todo:: <@doc_multiline paragraphElement.todoTag, indent, 10/><#rt>
    </#if>
    <#if paragraphElement.seeTag??>
.. seealso:: :obj:`${paragraphElement.seeTag.alias} <${paragraphElement.seeTag.link}>`<#rt>
    </#if>
    <#if paragraphElement.paramTag??>
:param ${paragraphElement.paramTag.name}: <@doc_multiline paragraphElement.paramTag.description, indent, 7/><#rt>
    </#if>
    <#if paragraphElement.isDeprecated>
        <#-- Keyword '.. deprecated::' is not used because it is ignored if no version is specified. -->
**Deprecated**<#rt>
    </#if>
</#macro>

<#macro doc_multiline multiline indent multiLineIndent>
    <#local I>${""?left_pad(indent * 4)}</#local>
    <#local M>${""?left_pad(multiLineIndent)}</#local>
    <#list multiline.lines as line>
        <#if !line?is_first>
${I}${M}<#rt>
        </#if>
        <#list line.lineElements as lineElement>
            <#if lineElement.docString??>
<#if !lineElement?is_first> </#if>${lineElement.docString}<#rt>
            </#if>
            <#if lineElement.seeTag??>
<#if !lineElement?is_first> </#if>:obj:`${lineElement.seeTag.alias} <${lineElement.seeTag.link}>`<#rt>
            </#if>
        </#list>
        <#if !line?is_last>

        </#if>
    </#list>
</#macro>
