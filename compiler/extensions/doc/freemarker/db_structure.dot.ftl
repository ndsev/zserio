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
<#if database.symbol.htmlLink??>
        URL="${database.symbol.htmlLink.htmlPage}#${database.symbol.htmlLink.htmlAnchor}";
</#if>
        tooltip="${database.symbol.htmlTitle}";

<#list database.tables as table>
        // table ${table.name}
        table_${database.symbol.name}_${table.name} [
    <#if table.typeSymbol.htmlLink??>
            URL="${table.typeSymbol.htmlLink.htmlPage}#${table.typeSymbol.htmlLink.htmlAnchor}",
    </#if>
            tooltip="${table.typeSymbol.htmlTitle}",
            label=<
                <TABLE BORDER="0" CELLBORDER="1" CELLSPACING="0" CELLPADDING="4">
                    <TR>
                        <TD WIDTH="565" BGCOLOR="gray" ALIGN="LEFT">
                            <TABLE BORDER="0">
    <#if table.packageName?has_content>
                                <TR>
                                    <TD><FONT FACE="monospace">${table.packageName}</FONT></TD>
                                </TR>
    </#if>
                                <TR>
                                    <TD><FONT FACE="monospace" COLOR="blue">${table.typeSymbol.name} ${table.name}</FONT></TD>
                                </TR>
                            </TABLE>
                        </TD>
                        <TD WIDTH="32" BGCOLOR="gray" ALIGN="LEFT">PK</TD>
                        <TD WIDTH="55" BGCOLOR="gray" ALIGN="LEFT">NULL</TD>
                    </TR>
                    <TR>
                        <TD BGCOLOR="white">
                            <TABLE BORDER="0">
    <#if table.fields?has_content>
        <#list table.fields as field>
                                <TR>
                                    <TD ALIGN="LEFT"><FONT FACE="monospace">${field.typeName}</FONT></TD>
                                    <TD ALIGN="LEFT"><FONT FACE="monospace">${field.name}</FONT></TD>
                                </TR>
        </#list>
    <#else>
                                <TR><TD></TD></TR>
    </#if>
                            </TABLE>
                        </TD>
                        <TD BGCOLOR="white">
                            <TABLE BORDER="0">
    <#if table.fields?has_content>
        <#list table.fields as field>
                                <TR>
                                    <TD><#if field.isPrimaryKey>&#215;<#else> &nbsp;</#if></TD>
                                </TR>
        </#list>
    <#else>
                                <TR><TD></TD></TR>
    </#if>
                            </TABLE>
                        </TD>
                        <TD BGCOLOR="white">
                            <TABLE BORDER="0">
    <#if table.fields?has_content>
        <#list table.fields as field>
                                <TR>
                                    <TD><#if field.isNullAllowed>&#215;<#else> &nbsp;</#if></TD>
                                </TR>
        </#list>
    <#else>
                                <TR><TD></TD></TR>
    </#if>
                            </TABLE>
                        </TD>
                    </TR>
                </TABLE>
            >
        ];

</#list>
    }; // end of database ${database.symbol.name}
}
