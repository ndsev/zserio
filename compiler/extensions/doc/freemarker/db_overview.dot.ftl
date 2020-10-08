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
    <#if database.symbol.htmlLink??>
        URL="${database.symbol.htmlLink.htmlPage}#${database.symbol.htmlLink.htmlAnchor}";
    </#if>
        tooltip="${database.symbol.htmlTitle}";

        // database tables
        ${database.symbol.name}_tables [
            label=<
                <TABLE BORDER="0" CELLBORDER="1" CELLSPACING="0" CELLPADDING="4">
                    <TR>
                        <TD VALIGN="BOTTOM" BGCOLOR="gray">Database Tables</TD>
                    </TR>
                    <TR>
                        <TD ALIGN="LEFT" BGCOLOR="white" WIDTH="840" >
                            <TABLE BORDER="0">
    <#if database.tables?has_content>
        <#list database.tables as table>
            <#if table.typeSymbol.htmlLink??>
                <#assign hRefCommand = "HREF=\"${table.typeSymbol.htmlLink.htmlPage}#${table.typeSymbol.htmlLink.htmlAnchor}\"">
            <#else>
                <#assign hRefCommand = "">
            </#if>
                                <TR>
                                    <TD ${hRefCommand} TITLE="${table.typeSymbol.htmlTitle}" ALIGN="LEFT"><FONT FACE="monospace">${table.fullTypeName}</FONT></TD>
                                    <TD ALIGN="LEFT"><FONT FACE="monospace" COLOR="blue">${table.name}</FONT></TD>
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
    }; // end of database ${database.symbol.name}
</#list>
}
