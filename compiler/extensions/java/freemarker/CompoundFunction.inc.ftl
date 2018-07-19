<#macro compound_functions compoundFunctionsData>
    <#list compoundFunctionsData.list as compoundFunction>
    public ${compoundFunction.returnTypeName} ${compoundFunction.name}()
    {
        <#if compoundFunction.returnTypeName?matches("java.math.BigInteger")>
        return ${compoundFunction.resultExpression};
        <#else>
        return (${compoundFunction.returnTypeName})(${compoundFunction.resultExpression});
        </#if>
    }

    </#list>
</#macro>
