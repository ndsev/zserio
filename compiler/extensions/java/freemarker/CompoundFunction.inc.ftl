<#macro compound_functions compoundFunctionsData>
    <#list compoundFunctionsData.list as compoundFunction>
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
