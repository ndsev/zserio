<#macro compound_functions compoundFunctionsData>
    <#list compoundFunctionsData.list as compoundFunction>
    public ${compoundFunction.returnTypeInfo.typeName} ${compoundFunction.name}()
    {
        <#if compoundFunction.returnTypeInfo.typeName?matches("java.math.BigInteger")>
        return ${compoundFunction.resultExpression};
        <#else>
        return (${compoundFunction.returnTypeInfo.typeName})(${compoundFunction.resultExpression});
        </#if>
    }

    </#list>
</#macro>
