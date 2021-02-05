package zserio.extension.python;

import zserio.ast.SqlDatabaseType;
import zserio.extension.common.OutputFileManager;
import zserio.extension.common.ZserioExtensionException;

class SqlDatabaseEmitter extends CompoundEmitter
{
    public SqlDatabaseEmitter(OutputFileManager outputFileManager, PythonExtensionParameters pythonParameters)
    {
        super(outputFileManager, pythonParameters);
    }

    @Override
    public void beginSqlDatabase(SqlDatabaseType sqlDatabaseType) throws ZserioExtensionException
    {
        if (getWithSqlCode())
        {
            final SqlDatabaseEmitterTemplateData templateData =
                    new SqlDatabaseEmitterTemplateData(getTemplateDataContext(), sqlDatabaseType);
            processCompoundTemplate(TEMPLATE_SOURCE_NAME, templateData, sqlDatabaseType);
        }
    }

    private static final String TEMPLATE_SOURCE_NAME = "SqlDatabase.py.ftl";
}
