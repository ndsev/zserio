<#include "symbol.inc.ftl">
/**
 * This dot file creates overview diagram for all databases.
 */
digraph Zserio
{
    node [shape=none];
    rankdir=LR;
    ranksep="5.5 equally";
<#list databases as database>

    // database ${database.symbol.name}
    subgraph cluster_${database.symbol.name}
    {
        fontsize="32";
        bgcolor="${database.colorName}";
        label="${database.symbol.name}";
        <@symbol_reference_url database.symbol/>;
        <@symbol_reference_tooltip database.symbol/>;

        // database tables
        ${database.symbol.name}_tables [
    <#outputformat "HTML"> 
            label=<
                <table border="0" cellborder="1" cellspacing="0" cellpadding="4">
                    <tr>
                        <td valign="bottom" bgcolor="gray">Database Tables</td>
                    </tr>
                    <tr>
                        <td align="left" bgcolor="white" width="840">
                            <table border="0">
        <#if database.tables?has_content>
            <#list database.tables as table>
                                <tr>
                                    <td align="left" <@symbol_reference_href_title table.typeSymbol/>><#rt>
                                      <#lt><font face="monospace">${table.fullTypeName}</font></td>
                                    <td align="left"><font face="monospace" COLOR="blue">${table.name}</font></td>
                                </tr>
            </#list>
        <#else>
                                <tr><td></td></tr>
        </#if>
                            </table>
                        </td>
                    </tr>
                </table>
            >
    </#outputformat>
        ];
    }; // end of database ${database.symbol.name}
</#list>
}
