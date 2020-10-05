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
        // TODO[mikir] to re-think dotLinksPrefix, it won't work
        super(outputPathName, extensionParameters, (dotLinksPrefix == null) ? ".." : dotLinksPrefix,
                withSvgDiagrams, dotExecutable, usedByCollector);

        final String directoryPrefix = getDotLinksPrefix() + "/";
        context = new TemplateDataContext(getWithSvgDiagrams(), getUsedByCollector(), getPackageMapper(),
                getResourceManager(), directoryPrefix + HTML_CONTENT_DIRECTORY,
                directoryPrefix + SYMBOL_COLLABORATION_DIRECTORY);

        databases = new ArrayList<SqlDatabaseType>();
    }

    @Override
    public void endRoot(Root root) throws ZserioEmitException
    {
        final Object templateData = new DbOverviewDotTemplateData(context, databases);
        final File outputDotFile = new File(getOutputPathName(),
                DB_OVERVIEW_DOT_DIRECTORY + File.separator + DB_OVERVIEW_DOT_FILE_NAME);
        final File outputSvgFile = new File(getOutputPathName(),
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

    private final TemplateDataContext context;
    private final List<SqlDatabaseType> databases;
}
