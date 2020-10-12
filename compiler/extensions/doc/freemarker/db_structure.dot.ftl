<#include "symbol.inc.ftl">
/**
 * This dot file creates structure diagram for database ${database.symbol.name}.
 */
digraph Zserio
{
    node [shape=none];
    rankdir=LR;
    ranksep="1.5 equally";

    // database ${database.symbol.name}
    subgraph cluster_${database.symbol.name}
    {
        fontsize="32";
        bgcolor="${database.colorName}";
        label="${database.symbol.name}";
        <@symbol_reference_url database.symbol/>;
        <@symbol_reference_tooltip database.symbol/>;

<#list database.tables as table>
        // table ${table.name}
        table_${database.symbol.name}_${table.name} [
            <@symbol_reference_url table.typeSymbol/>;
            <@symbol_reference_tooltip table.typeSymbol/>;
    <#outputformat "HTML"> 
            label=<
                <table border="0" cellborder="1" cellspacing="0" cellpadding="4">
                    <tr>
                        <td width="565" bgcolor="gray" align="left">
                            <table border="0">
        <#if table.packageName?has_content>
                                <tr>
                                    <td><font face="monospace">${table.packageName}</font></td>
                                </tr>
        </#if>
                                <tr>
                                    <td><font face="monospace" color="blue" <@symbol_reference_href_title table.typeSymbol/>><#rt>
                                      <#lt>${table.typeSymbol.name} ${table.name}</font></td>
                                </tr>
                            </table>
                        </td>
                        <td width="32" bgcolor="gray" align="left">PK</td>
                        <td width="55" bgcolor="gray" align="left">NULL</td>
                    </tr>
                    <tr>
                        <td bgcolor="white">
                            <table border="0">
        <#if table.fields?has_content>
            <#list table.fields as field>
                                <tr>
                                    <td align="left"><font face="monospace" <@symbol_reference_href_title field.typeSymbol/>><#rt>
                                      <#lt>${field.typeSymbol.name}</font></td>
                                    <td align="left"><font face="monospace">${field.name}</font></td>
                                </tr>
            </#list>
        <#else>
                                <tr><td></td></tr>
        </#if>
                            </table>
                        </td>
                        <td bgcolor="white">
                            <table border="0">
        <#if table.fields?has_content>
            <#list table.fields as field>
                                <tr>
                                    <td><#if field.isPrimaryKey>&#215;<#else> &nbsp;</#if></td>
                                </tr>
            </#list>
        <#else>
                                <tr><td></td></tr>
        </#if>
                            </table>
                        </td>
                        <td bgcolor="white">
                            <table border="0">
        <#if table.fields?has_content>
            <#list table.fields as field>
                                <tr>
                                    <td><#if field.isNullAllowed>&#215;<#else> &nbsp;</#if></td>
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

</#list>
    }; // end of database ${database.symbol.name}
}
