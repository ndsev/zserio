<#-- This macro generates HTML source for the documentation comment. -->
<#macro doc_comment doc>
    <#if doc.paragraphs?size == 0>
    <div class="docuTag">&lt;<i>no documentation found</i>&gt;</div>
    <#else>
        <#list doc.paragraphs as paragraph>
            <#list paragraph.elements as element>
                <#if element.multiline??>
    <div class="docuTag">
        <@doc_multiline_node element.multiline/>
    </div>
                </#if>

                <#if element.todoTag??>
    <div class="docuTag">
        <span>Todo:</span>
        <@doc_multiline_node element.todoTag/>
    </div>
                </#if>

                <#if element.seeTag??>
    <div class="docuTag">
        <span>See:</span> <a href="${element.seeTag.url}">${element.seeTag.alias}</a>
    </div>
                </#if>

                <#if element.paramTag??>
    <div class="docuTag">
        <span>Param:</span> <code>${element.paramTag.name}</code>
        <@doc_multiline_node element.paramTag.description/>
    </div>
                </#if>
            </#list>
        </#list>

        <#if doc.isDeprecated>
    <div class="docuTag">
        <span>Note:</span> This element is deprecated and is going to be invalid in the future versions.
    </div>
        </#if>
    </#if>
</#macro>

<#macro doc_multiline_node multilineNode>
    <#list multilineNode.docLineElements as docLineElement>
        <#if docLineElement.docString??>
        ${docLineElement.docString}
        </#if>
        <#if docLineElement.seeTag??>
        <a href="${docLineElement.seeTag.url}">${docLineElement.seeTag.alias}</a>
        </#if>
    </#list>
</#macro> 