<#macro compound_functions_declaration compoundFunctionsData>
    <#if compoundFunctionsData.list?has_content>
        <#list compoundFunctionsData.list as compoundFunction>
    ${compoundFunction.returnArgumentTypeName} ${compoundFunction.name}() const;
            <#if withWriterCode && !compoundFunction.isSimpleReturnType>
    ${compoundFunction.returnTypeName}& ${compoundFunction.name}();
            </#if>

        </#list>
    </#if>
</#macro>

<#macro compound_functions_definition compoundName compoundFunctionsData>
    <#list compoundFunctionsData.list as compoundFunction>
${compoundFunction.returnArgumentTypeName} ${compoundName}::${compoundFunction.name}() const
{
    return <#if compoundFunction.isSimpleReturnType>static_cast<${compoundFunction.returnArgumentTypeName}>(</#if><#rt>
            <#lt>${compoundFunction.resultExpression}<#if compoundFunction.isSimpleReturnType>)</#if>;
}

<#if withWriterCode && !compoundFunction.isSimpleReturnType>
${compoundFunction.returnTypeName}& ${compoundName}::${compoundFunction.name}()
{
    return ${compoundFunction.resultExpression};
}

</#if>
    </#list>
</#macro>
