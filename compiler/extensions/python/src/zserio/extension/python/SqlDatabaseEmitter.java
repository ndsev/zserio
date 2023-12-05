package zserio.extension.python;

import zserio.ast.SqlDatabaseType;
import zserio.extension.common.OutputFileManager;
import zserio.extension.common.ZserioExtensionException;

/**
 * SQL database emitter.
 */
final class SqlDatabaseEmitter extends PythonDefaultEmitter
{
    public SqlDatabaseEmitter(OutputFileManager outputFileManager, PythonExtensionParameters pythonParameters)
    {
        super(outputFileManager, pythonParameters);
    }

    @Override
    public void beginSqlDatabase(SqlDatabaseType sqlDatabaseType) throws ZserioExtensionException
    {
        if (!getWithSqlCode())
            return;

        final SqlDatabaseEmitterTemplateData templateData =
                new SqlDatabaseEmitterTemplateData(getTemplateDataContext(), sqlDatabaseType);
        processSourceTemplate(TEMPLATE_SOURCE_NAME, templateData, sqlDatabaseType);
    }

    static final String TEMPLATE_SOURCE_NAME = "SqlDatabase.py.ftl";
}
