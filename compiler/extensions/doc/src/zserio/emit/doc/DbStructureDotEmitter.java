package zserio.emit.doc;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import zserio.ast.Root;
import zserio.ast.SqlDatabaseType;
import zserio.emit.common.ZserioEmitException;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

/**
 * Emits the one DOT file per each database with all tables and theirs connections.
 */
public class DbStructureDotEmitter extends DefaultDocEmitter
{
    /**
     * Creates an instance of the Database Overview Dot emitter.
     *
     * @param docPath              Path to the root of generated documentation.
     * @param dotLinksPrefix       Prefix for doc links or null to use links to locally generated doc.
     * @param withSvgDiagrams True to enable dot files conversion to svg format.
     * @param dotExecutable        Dot executable to use for conversion or null to use dot exe on path.
     */
    public DbStructureDotEmitter(String docPath, String dotLinksPrefix, boolean withSvgDiagrams,
                                 String dotExecutable)
    {
        databaseList = new ArrayList<SqlDatabaseType>();
        this.docPath = docPath;
        this.dotLinksPrefix = (dotLinksPrefix == null) ? ".." : dotLinksPrefix;
        this.withSvgDiagrams = withSvgDiagrams;
        this.dotExecutable = dotExecutable;
    }

    @Override
    public void beginSqlDatabase(SqlDatabaseType sqlDatabaseType)
    {
        databaseList.add(sqlDatabaseType);
    }

    @Override
    public void endRoot(Root root) throws ZserioEmitException
    {
        int databaseIndex = 0;
        for (SqlDatabaseType database : databaseList)
        {
            final File outputFile = DocEmitterTools.getDbStructureDotFile(docPath, database);
            final String databaseColor = DocEmitterTools.getDatabaseColor(databaseIndex);
            emit(database, databaseColor, outputFile);
            if (withSvgDiagrams)
            {
                if (!DotFileConvertor.convertToSvg(dotExecutable, outputFile,
                        DocEmitterTools.getDbStructureSvgFile(docPath, database)))
                {
                    throw new ZserioEmitException("Failure to convert '" + outputFile +
                            "' to SVG format!");
                }
            }

            databaseIndex++;
        }
    }

    private void emit(SqlDatabaseType database, String databaseColor, File outputFile)
            throws ZserioEmitException
    {
        try
        {
            Configuration fmConfig = new Configuration(Configuration.VERSION_2_3_28);
            fmConfig.setClassForTemplateLoading(DbStructureDotEmitter.class, "/freemarker/");

            Template fmTemplate = fmConfig.getTemplate("doc/db_structure.dot.ftl");

            openOutputFile(outputFile);
            fmTemplate.process(new DbStructureDotTemplateData(database, databaseColor, dotLinksPrefix), writer);
            writer.close();
        }
        catch (IOException exception)
        {
            throw new ZserioEmitException(exception.getMessage());
        }
        catch (TemplateException exception)
        {
            throw new ZserioEmitException(exception.getMessage());
        }
    }

    private final List<SqlDatabaseType> databaseList;
    private final String                docPath;
    private final String                dotLinksPrefix;
    private final boolean               withSvgDiagrams;
    private final String                dotExecutable;
}
