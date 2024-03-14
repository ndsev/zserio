<#macro function_return_view_type compoundFunction>
    <#if compoundFunction.returnTypeInfo.isCompound>
        ${compoundFunction.returnTypeInfo.typeFullName}::View<#t>
    <#else>
        ${compoundFunction.returnTypeInfo.typeFullName}<#t>
    </#if>
</#macro>
