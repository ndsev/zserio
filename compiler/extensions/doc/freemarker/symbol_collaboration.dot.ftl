<#include "symbol.inc.ftl">
/**
 * This dot file creates symbol collaboration diagram for ${symbol.name}.
 */
digraph ZSERIO
{
    node [shape=box, fontsize=10];
    rankdir="LR";
    fontsize=10;
    tooltip="${symbol.name} collaboration diagram";
<#list packages as package>

    subgraph "cluster_${package.symbol.name}"
    {
        style="dashed, rounded";
        label="${package.symbol.name}";
        tooltip="${package.symbol.name}";

    <#list package.symbols as packageSymbol>
        <#if packageSymbol.htmlLink??>
            <#assign isMainSymbol = (packageSymbol.name == symbol.name)>
        "<@symbol_node_name packageSymbol/>" [<#if isMainSymbol>style="filled", fillcolor="#0000000D", </#if><#rt>
            <#lt>target="_parent", label=<<font face="monospace"><#rt>
            <#lt><@symbol_reference_label packageSymbol, "center"/></font>>];
        </#if>
    </#list>
    }
</#list>

<#list relations as relation>
    "<@symbol_node_name relation.symbolFrom/>" -> "<@symbol_node_name relation.symbolTo/>" [label="uses", fontsize=10];
</#list>
}
