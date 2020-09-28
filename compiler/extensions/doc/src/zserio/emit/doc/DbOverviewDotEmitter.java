package zserio.emit.doc;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import zserio.ast.Root;
import zserio.ast.SqlDatabaseType;
import zserio.emit.common.ZserioEmitException;
import zserio.tools.Parameters;

/**
 * Emits the DOT file with overview of all databases and tables with connections.
 */
class DbOverviewDotEmitter extends DotDefaultEmitter
{
    public DbOverviewDotEmitter(String outputPathName, Parameters extensionParameters, String dotLinksPrefix,
            boolean withSvgDiagrams, String dotExecutable, UsedByCollector usedByCollector)
    {
        super(extensionParameters, dotLinksPrefix, withSvgDiagrams, dotExecutable, usedByCollector);

        this.outputPathName = outputPathName;
        databases = new ArrayList<SqlDatabaseType>();
    }

    @Override
    public void endRoot(Root root) throws ZserioEmitException
    {
        final Object templateData = new DbOverviewDotTemplateData(databases, getDotLinksPrefix());
        final File outputDotFile = new File(outputPathName,
                DB_OVERVIEW_DOT_DIRECTORY + File.separator + DB_OVERVIEW_DOT_FILE_NAME);
        final File outputSvgFile = new File(outputPathName,
                DB_OVERVIEW_DOT_DIRECTORY + File.separator + DB_OVERVIEW_SVG_FILE_NAME);
        processDotTemplate(TEMPLATE_SOURCE_NAME, templateData, outputDotFile, outputSvgFile);
    }

    @Override
    public void beginSqlDatabase(SqlDatabaseType sqlDatabaseType)
    {
        databases.add(sqlDatabaseType);
    }

    private static final String DB_OVERVIEW_DOT_DIRECTORY = "db_overview";
    private static final String DB_OVERVIEW_DOT_FILE_NAME = "overview.dot";
    private static final String DB_OVERVIEW_SVG_FILE_NAME = "overview.svg";

    private static final String TEMPLATE_SOURCE_NAME = "db_overview.dot.ftl";

    private final String outputPathName;
    private final List<SqlDatabaseType> databases;
}
