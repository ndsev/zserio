package zserio.emit.doc;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import zserio.ast.PackageName;
import zserio.ast.Root;
import zserio.ast.SqlDatabaseType;
import zserio.emit.common.PackageMapper;
import zserio.emit.common.ZserioEmitException;
import zserio.tools.Parameters;
import zserio.tools.StringJoinUtil;

/**
 * Emits the one DOT file per each database with all tables and theirs connections.
 */
class DbStructureDotEmitter extends DotDefaultEmitter
{
    public DbStructureDotEmitter(String outputPathName, Parameters extensionParameters, String dotLinksPrefix,
            boolean withSvgDiagrams, String dotExecutable, UsedByCollector usedByCollector)
    {
        // TODO[mikir] to re-think dotLinksPrefix, it won't work
        super(outputPathName, extensionParameters, (dotLinksPrefix == null) ? "../.." : dotLinksPrefix,
                withSvgDiagrams, dotExecutable, usedByCollector);

        final String directoryPrefix = getDotLinksPrefix() + File.separator;
        context = new TemplateDataContext(getWithSvgDiagrams(), getUsedByCollector(), getPackageMapper(),
                getResourceManager(), directoryPrefix + HTML_CONTENT_DIRECTORY,
                directoryPrefix + SYMBOL_COLLABORATION_DIRECTORY, ".");

        databaseList = new ArrayList<SqlDatabaseType>();
    }

    @Override
    public void endRoot(Root root) throws ZserioEmitException
    {
        for (SqlDatabaseType database : databaseList)
        {
            final Object templateData = new DbStructureDotTemplateData(context, database);
            final String dotHtmlLink = getDotDbStructureHtmlLink(database, getPackageMapper(),
                    DB_STRUCTURE_DIRECTORY);
            final File outputDotFile = new File(getOutputPathName(), dotHtmlLink);
            final String svgHtmlLink = getSvgDbStructureHtmlLink(database, getPackageMapper(),
                    DB_STRUCTURE_DIRECTORY);
            final File outputSvgFile = new File(getOutputPathName(), svgHtmlLink);
            processDotTemplate(TEMPLATE_SOURCE_NAME, templateData, outputDotFile, outputSvgFile);
        }
    }

    @Override
    public void beginSqlDatabase(SqlDatabaseType sqlDatabaseType)
    {
        databaseList.add(sqlDatabaseType);
    }

    public static String getSvgDbStructureHtmlLink(SqlDatabaseType database, PackageMapper packageMapper,
            String dbStructureDirectory)
    {
        return getDbStructureHtmlLinkBase(database, packageMapper, dbStructureDirectory) + SVG_FILE_EXTENSION;
    }

    private static String getDotDbStructureHtmlLink(SqlDatabaseType database, PackageMapper packageMapper,
            String dbStructureDirectory)
    {
        return getDbStructureHtmlLinkBase(database, packageMapper, dbStructureDirectory) + DOT_FILE_EXTENSION;
    }

    private static String getDbStructureHtmlLinkBase(SqlDatabaseType database, PackageMapper packageMapper,
            String dbStructureDirectory)
    {
        final PackageName packageName = packageMapper.getPackageName(database);
        final String name = database.getName();
        final String packageNameString = ((packageName.isEmpty()) ? DEFAULT_PACKAGE_FILE_NAME :
            packageName.toString());

        return StringJoinUtil.joinStrings(dbStructureDirectory, packageNameString, name, File.separator);
    }

    private static final String TEMPLATE_SOURCE_NAME = "db_structure.dot.ftl";

    private final TemplateDataContext context;
    private final List<SqlDatabaseType> databaseList;
}
