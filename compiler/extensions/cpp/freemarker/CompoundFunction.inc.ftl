<#macro compound_functions_declaration compoundFunctionsData>
    <#if compoundFunctionsData.list?has_content>
        <#list compoundFunctionsData.list as compoundFunction>

            <#if compoundFunction.returnTypeInfo.isSimple>
                <#if withCodeComments>
    /**
     * Implementation of the function ${compoundFunction.name}.
     *
                    <#if compoundFunction.docComments??>
     * \b Description
     *
     <@doc_comments_inner compoundFunction.docComments, 1/>
     *
                    </#if>
     * \return Result of the function ${compoundFunction.name}.
     */
                </#if>
    ${compoundFunction.returnTypeInfo.typeFullName} ${compoundFunction.name}() const;
            <#else>
                <#if withCodeComments>
    /**
     * Implementation of the function ${compoundFunction.name} using const reference.
     *
                    <#if compoundFunction.docComments??>
     * \b Description
     *
     <@doc_comments_inner compoundFunction.docComments, 1/>
     *
                    </#if>
     * \return Const reference to the result of the function ${compoundFunction.name}.
     */
                </#if>
    const ${compoundFunction.returnTypeInfo.typeFullName}& ${compoundFunction.name}() const;
                <#if withWriterCode>
                    <#if withCodeComments>

    /**
     * Implementation of the function ${compoundFunction.name} using reference.
     *
     * This method can be called internally during setting of offsets.
     *
                        <#if compoundFunction.docComments??>
     * \b Description
     *
     <@doc_comments_inner compoundFunction.docComments, 1/>
     *
                        </#if>
     * \return Reference to the result of the function ${compoundFunction.name}.
     */
                    </#if>
    ${compoundFunction.returnTypeInfo.typeFullName}& ${compoundFunction.name}();
                </#if>
            </#if>
        </#list>
    </#if>
</#macro>

<#macro compound_functions_definition compoundName compoundFunctionsData>
    <#list compoundFunctionsData.list as compoundFunction>
        <#if compoundFunction.returnTypeInfo.isSimple>
${compoundFunction.returnTypeInfo.typeFullName} ${compoundName}::${compoundFunction.name}() const
        <#else>
const ${compoundFunction.returnTypeInfo.typeFullName}& ${compoundName}::${compoundFunction.name}() const
        </#if>
{
    return <#if compoundFunction.returnTypeInfo.isSimple>static_cast<${compoundFunction.returnTypeInfo.typeFullName}>(</#if><#rt>
            <#lt>${compoundFunction.resultExpression}<#if compoundFunction.returnTypeInfo.isSimple>)</#if>;
}

<#if withWriterCode && !compoundFunction.returnTypeInfo.isSimple>
${compoundFunction.returnTypeInfo.typeFullName}& ${compoundName}::${compoundFunction.name}()
{
    return ${compoundFunction.resultExpression};
}

</#if>
    </#list>
</#macro>
