<#-- This macro generates HTML source for the documentation comment. -->
<#macro doc_comment doc>
    <#if doc.paragraphList?size == 0>
    <div class="docuTag">&lt;<i>no documentation found</i>&gt;</div>
    <#else>
        <#list doc.paragraphList as paragraph>
    <div class="docuTag">
            <#list paragraph.docTextList as docText>
                <#if docText.docString??>
        ${docText.docString}
                </#if>
                <#if docText.seeTag??>
        <a href="${docText.seeTag.url}">${docText.seeTag.alias}</a>
                </#if>
            </#list>
    </div>
        </#list>

        <#list doc.tagTodoList as tagTodo>
    <div class="docuTag">
        <span>Todo:</span>
            <#list tagTodo.textList as text>
        ${text}
            </#list>
    </div>
        </#list>

        <#list doc.tagSeeList as tagSee>
    <div class="docuTag">
        <span>See:</span> <a href="${tagSee.url}">${tagSee.alias}</a>
    </div>
        </#list>

        <#list doc.tagParamList as tagParam>
    <div class="docuTag">
        <span>Param:</span> <code>${tagParam.name}</code>
                <#list tagParam.descriptionList as description>
        ${description}
                </#list>
    </div>
        </#list>

        <#if doc.isDeprecated>
    <div class="docuTag">
        <span>Note:</span> This element is deprecated and is going to be invalid in the future versions.
    </div>
        </#if>
    </#if>
</#macro>
