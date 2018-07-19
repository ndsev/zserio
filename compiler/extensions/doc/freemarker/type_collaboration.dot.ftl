/**
 * This dot file creates type collaboration diagram for ${typeName}.
 */
digraph ZSERIO
{
    node [shape=box, fontsize=11];
    rankdir="LR";
    fontsize="11";
<#list packageList as package>

    subgraph cluster_${package.name?replace(".", "_")}
    {
        style="dashed, rounded";
        label="${package.name}";
        tooltip="${package.name}";

    <#list package.typeList as type>
        <#if type.docUrl?has_content>
            <#assign isMainType = (type.name == typeName)>
        "  ${type.name}  " [<#if isMainType>style="filled", fillcolor="#F2F2FF", </#if>URL="${type.docUrl}", target="_parent"];
        </#if>
    </#list>
    }
</#list>

<#list relationList as relation>
    "  ${relation.typeNameFrom}  " -> "  ${relation.typeNameTo}  " [label="uses", fontsize="11"];
</#list>
}
