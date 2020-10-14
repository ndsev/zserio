<#include "symbol.inc.ftl">
/**
 * This dot file creates structure diagram for database ${symbol.name}.
 */
digraph Zserio
{
    node [shape=none, fontsize=11];
    rankdir=LR;
    ranksep="1.5 equally";

    // database ${symbol.name}
    subgraph cluster_${symbol.name}
    {
        fontsize="16";
        style="dashed, rounded";
        label="${symbol.name}";
        <@symbol_reference_url symbol/>;
        <@symbol_reference_tooltip symbol/>;
        target="_parent";

<#list tables as table>
        // table ${table.name}
        table_${symbol.name}_${table.name} [
            <@symbol_reference_url table.typeSymbol/>;
            <@symbol_reference_tooltip table.typeSymbol/>;
            target="_parent";
    <#outputformat "HTML"> 
            label=<
                <table border="0" cellborder="1" cellspacing="0" cellpadding="4">
                    <tr>
                        <td width="505" bgcolor="#F2F2FF" align="left">
                            <table border="0">
        <#if table.packageName?has_content>
                                <tr>
                                    <td><font face="monospace">${table.packageName}</font></td>
                                </tr>
        </#if>
                                <tr>
                                    <td <@symbol_reference_href_title table.typeSymbol/> target="_parent"><#rt>
                                      <#lt><font face="monospace">${table.typeSymbol.name} ${table.name}</font></td>
                                </tr>
                            </table>
                        </td>
                        <td width="25" bgcolor="#F2F2FF" align="left">PK</td>
                        <td width="42" bgcolor="#F2F2FF" align="left">NULL</td>
                    </tr>
                    <tr>
                        <td bgcolor="white">
                            <table border="0">
        <#if table.fields?has_content>
            <#list table.fields as field>
                                <tr>
                                    <td align="left" <@symbol_reference_href_title field.typeSymbol/> target="_parent"><#rt>
                                      <#lt><font face="monospace">${field.typeSymbol.name}</font></td>
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
    }; // end of database ${symbol.name}
}
