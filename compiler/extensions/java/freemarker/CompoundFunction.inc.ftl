<#macro compound_functions compoundFunctionsData>
    <#list compoundFunctionsData.list as compoundFunction>
        <#if withCodeComments>
    /**
     * Implementation of the function ${compoundFunction.name}.
            <#if compoundFunction.docComments??>
     * <p>
     * <b>Description:</b>
     * <br>
     <@doc_comments_inner compoundFunction.docComments, 1/>
     *
        <#else>
     *
        </#if>
     * @return Result of the function ${compoundFunction.name}.
     */
        </#if>
    public ${compoundFunction.returnTypeInfo.typeFullName} ${compoundFunction.name}()
    {
        <#if compoundFunction.returnTypeInfo.typeFullName?matches("java.math.BigInteger")>
        return ${compoundFunction.resultExpression};
        <#else>
        return (${compoundFunction.returnTypeInfo.typeFullName})(${compoundFunction.resultExpression});
        </#if>
    }

    </#list>
</#macro>
