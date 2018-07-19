/**
 * This dot file creates overview diagram for all databases.
 */
digraph Zserio
{
    node [shape=none];
    rankdir=LR;
    ranksep="5.5 equally";

<#list databaseList as database>
    // database ${database.name}
    subgraph cluster_${database.name}
    {
        fontsize="32";
        bgcolor="${database.colorName}";
        label="${database.name}";
    <#if database.docUrl?has_content>
        URL="${database.docUrl}";
    </#if>
        tooltip="${database.name}";

        // database tables
        ${database.name}_tables [
            label=<
                <TABLE BORDER="0" CELLBORDER="1" CELLSPACING="0" CELLPADDING="4">
                    <TR>
                        <TD VALIGN="BOTTOM" BGCOLOR="gray">Database Tables</TD>
                    </TR>
                    <TR>
                        <TD ALIGN="LEFT" BGCOLOR="white" WIDTH="840" >
                            <TABLE BORDER="0">
    <#if database.tableList?has_content>
        <#list database.tableList as table>
            <#if table.docUrl?has_content>
                <#assign hRefCommand = "HREF=\"${table.docUrl}\"">
            <#else>
                <#assign hRefCommand = "">
            </#if>
                                <TR>
                                    <TD ${hRefCommand} ALIGN="LEFT"><FONT FACE="monospace">${table.fullTypeName}</FONT></TD>
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
    }; // end of database ${database.name}

</#list>
}
