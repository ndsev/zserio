/**
 * This dot file creates symbol collaboration diagram for ${symbolName}.
 */
digraph ZSERIO
{
    node [shape=box, fontsize=11];
    rankdir="LR";
    fontsize="11";
<#list packages as package>

    subgraph cluster_${package.name?replace(".", "_")}
    {
        style="dashed, rounded";
        label="${package.name}";
        tooltip="${package.name}";

    <#list package.symbols as symbol>
        <#if symbol.htmlLink??>
            <#assign isMainSymbol = (symbol.name == symbolName)>
        "  ${symbol.name}  " [<#if isMainSymbol>style="filled", fillcolor="#F2F2FF", </#if><#rt>
            <#lt>URL="${symbol.htmlLink.htmlPage}#${symbol.htmlLink.htmlAnchor}", tooltip="${symbol.htmlTitle}" target="_parent"];
        </#if>
    </#list>
    }
</#list>

<#list relations as relation>
    "  ${relation.symbolNameFrom}  " -> "  ${relation.symbolNameTo}  " [label="uses", fontsize="11"];
</#list>
}
