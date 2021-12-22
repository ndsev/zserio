<#macro compound_functions_declaration compoundFunctionsData>
    <#if compoundFunctionsData.list?has_content>
        <#list compoundFunctionsData.list as compoundFunction>
            <#if compoundFunction.returnTypeInfo.isSimple>
    ${compoundFunction.returnTypeInfo.typeName} ${compoundFunction.name}() const;
            <#else>
    const ${compoundFunction.returnTypeInfo.typeName}& ${compoundFunction.name}() const;
                <#if withWriterCode>
    ${compoundFunction.returnTypeInfo.typeName}& ${compoundFunction.name}();
                </#if>
            </#if>

        </#list>
    </#if>
</#macro>

<#macro compound_functions_definition compoundName compoundFunctionsData>
    <#list compoundFunctionsData.list as compoundFunction>
        <#if compoundFunction.returnTypeInfo.isSimple>
${compoundFunction.returnTypeInfo.typeName} ${compoundName}::${compoundFunction.name}() const
        <#else>
const ${compoundFunction.returnTypeInfo.typeName}& ${compoundName}::${compoundFunction.name}() const
        </#if>
{
    return <#if compoundFunction.returnTypeInfo.isSimple>static_cast<${compoundFunction.returnTypeInfo.typeName}>(</#if><#rt>
            <#lt>${compoundFunction.resultExpression}<#if compoundFunction.returnTypeInfo.isSimple>)</#if>;
}

<#if withWriterCode && !compoundFunction.returnTypeInfo.isSimple>
${compoundFunction.returnTypeInfo.typeName}& ${compoundName}::${compoundFunction.name}()
{
    return ${compoundFunction.resultExpression};
}

</#if>
    </#list>
</#macro>
