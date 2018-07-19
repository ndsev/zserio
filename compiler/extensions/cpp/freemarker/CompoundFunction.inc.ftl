<#include "Inspector.inc.ftl">
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
    return static_cast<${compoundFunction.returnTypeName}>(${compoundFunction.resultExpression});
}

    </#list>
</#macro>

<#macro compound_functions_write_tree rootPackageName compoundFunctionsData>
    <#if compoundFunctionsData.list?has_content>

        <#if compoundFunctionsData.list?size gt 1>
    _tree.reserveZserioFunctions(${compoundFunctionsData.list?size});
        </#if>
        <#list compoundFunctionsData.list as compoundFunction>
            <#local zserioFunctionName>_${compoundFunction.name}Function</#local>
            <#local functionZserioTypeName>${rootPackageName}::InspectorZserioTypeNames::<@inspector_zserio_type_name compoundFunction.zserioReturnTypeName/></#local>
            <#local functionZserioName>${rootPackageName}::InspectorZserioNames::<@inspector_zserio_name compoundFunction.name/></#local>
    zserio::BlobInspectorNode::ZserioFunction& ${zserioFunctionName} = _tree.createZserioFunction(
        ${functionZserioTypeName},
        ${functionZserioName});
    ${zserioFunctionName}.returnValue.set(${compoundFunction.name}());
        </#list>
    </#if>
</#macro>
