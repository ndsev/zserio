<#macro compound_functions_declaration compoundFunctionsData>
    <#if compoundFunctionsData.list?has_content>
        <#list compoundFunctionsData.list as compoundFunction>
    ${compoundFunction.returnTypeName} ${compoundFunction.name}() const;
        </#list>

    </#if>
</#macro>

<#macro compound_functions_definition compoundName compoundFunctionsData>
    <#list compoundFunctionsData.list as compoundFunction>
${compoundFunction.returnTypeName} ${compoundName}::${compoundFunction.name}() const
{
    return <#if compoundFunction.isSimpleReturnType>static_cast<${compoundFunction.returnTypeName}>(</#if><#rt>
            <#lt>${compoundFunction.resultExpression}<#if compoundFunction.isSimpleReturnType>)</#if>;
}

    </#list>
</#macro>
