<#include "symbol.inc.ftl">
/**
 * This dot file creates symbol collaboration diagram for ${symbolName}.
 */
digraph ZSERIO
{
    node [shape=box, fontsize=10];
    rankdir="LR";
    fontsize="10";
    tooltip="${symbolName} collaboration diagram";
<#list packages as package>

    subgraph cluster_${package.name?replace(".", "_")}
    {
        style="dashed, rounded";
        label="${package.name}";
        tooltip="${package.name}";

    <#list package.namedSymbols as namedSymbol>
        <#if namedSymbol.symbol.htmlLink??>
            <#assign isMainSymbol = (namedSymbol.name == symbolName)>
        "${namedSymbol.name}" [<#if isMainSymbol>style="filled", fillcolor="#F2F2FF", </#if>target="_parent", <#rt>
            <#lt>label=<<font face="monospace"><@symbol_reference_label namedSymbol.symbol "center"/></font>>];
        </#if>
    </#list>
    }
</#list>

<#list relations as relation>
    "${relation.symbolNameFrom}" -> "${relation.symbolNameTo}" [label="uses", fontsize="10"];
</#list>
}
