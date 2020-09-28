package zserio.emit.doc;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import zserio.ast.Root;
import zserio.ast.SqlDatabaseType;
import zserio.emit.common.ZserioEmitException;
import zserio.tools.Parameters;

/**
 * Emits the one DOT file per each database with all tables and theirs connections.
 */
class DbStructureDotEmitter extends DotDefaultEmitter
{
    public DbStructureDotEmitter(String outputPathName, Parameters extensionParameters, String dotLinksPrefix,
            boolean withSvgDiagrams, String dotExecutable, UsedByCollector usedByCollector)
    {
        super(extensionParameters, dotLinksPrefix, withSvgDiagrams, dotExecutable, usedByCollector);

        this.outputPathName = outputPathName;
        databaseList = new ArrayList<SqlDatabaseType>();
    }

    @Override
    public void endRoot(Root root) throws ZserioEmitException
    {
        int databaseIndex = 0;
        for (SqlDatabaseType database : databaseList)
        {
            final Object templateData = new DbStructureDotTemplateData(database, databaseIndex,
                    getDotLinksPrefix());
            final File outputDotFile = new File(outputPathName, DB_STRUCTURE_DOT_DIRECTORY +
                    File.separator + database.getName() + DOT_FILE_EXTENSION);
            final File outputSvgFile = new File(outputPathName, DB_STRUCTURE_DOT_DIRECTORY +
                    File.separator + database.getName() + SVG_FILE_EXTENSION);
            processDotTemplate(TEMPLATE_SOURCE_NAME, templateData, outputDotFile, outputSvgFile);

            databaseIndex++;
        }
    }

    @Override
    public void beginSqlDatabase(SqlDatabaseType sqlDatabaseType)
    {
        databaseList.add(sqlDatabaseType);
    }

    private static final String DB_STRUCTURE_DOT_DIRECTORY = "db_structure";

    private static final String TEMPLATE_SOURCE_NAME = "db_structure.dot.ftl";

    private final String outputPathName;
    private final List<SqlDatabaseType> databaseList;
}
