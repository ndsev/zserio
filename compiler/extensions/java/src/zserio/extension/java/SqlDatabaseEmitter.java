package zserio.extension.java;

import zserio.ast.SqlDatabaseType;
import zserio.extension.common.OutputFileManager;
import zserio.extension.common.PackedTypesCollector;
import zserio.extension.common.ZserioExtensionException;

/**
 * SQL database emitter.
 */
final class SqlDatabaseEmitter extends JavaDefaultEmitter
{
    public SqlDatabaseEmitter(OutputFileManager outputFileManager, JavaExtensionParameters javaParameters,
            PackedTypesCollector packedTypesCollector)
    {
        super(outputFileManager, javaParameters, packedTypesCollector);
    }

    @Override
    public void beginSqlDatabase(SqlDatabaseType sqlDatabaseType) throws ZserioExtensionException
    {
        if (getWithSqlCode())
        {
            final Object templateData = new SqlDatabaseEmitterTemplateData(getTemplateDataContext(),
                    sqlDatabaseType);
            processTemplate(TEMPLATE_NAME, templateData, sqlDatabaseType);
        }
    }

    private static final String TEMPLATE_NAME = "SqlDatabase.java.ftl";
}
