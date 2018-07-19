<#-- This macro generates HTML source for the documentation comment. -->
<#macro doc_comment doc>
    <#if doc.paragraphList?size == 0>
    <div class="docuTag">&lt;<i>no documentation found</i>&gt;</div>
    <#else>
        <#list doc.paragraphList as paragraph>
            <#if paragraph.paragraphTextList?size != 0>
    <div class="docuTag">
                <#list paragraph.paragraphTextList as paragraphText>
                    <#list paragraphText.textList as text>
        ${text}
                    </#list>
                    <#list paragraphText.tagSeeList as tagSee>
        <a href="${tagSee.url}">${tagSee.alias}</a>
                    </#list>
                </#list>
    </div>
            </#if>

            <#list paragraph.tagTodoList as tagTodo>
    <div class="docuTag">
        <span>Todo:</span>
                <#list tagTodo.textList as text>
        ${text}
                </#list>
    </div>
            </#list>

            <#list paragraph.tagSeeList as tagSee>
    <div class="docuTag">
        <span>See:</span> <a href="${tagSee.url}">${tagSee.alias}</a>
    </div>
            </#list>

            <#list paragraph.tagParamList as tagParam>
    <div class="docuTag">
        <span>Param:</span> <code>${tagParam.name}</code>
                <#list tagParam.descriptionList as description>
        ${description}
                </#list>
    </div>
            </#list>
        </#list>

        <#if doc.isDeprecated>
    <div class="docuTag">
        <span>Note:</span> This element is deprecated and is going to be invalid in the future versions.
    </div>
        </#if>
    </#if>
</#macro>
