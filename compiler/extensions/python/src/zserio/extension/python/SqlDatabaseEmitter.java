package zserio.extension.python;

import zserio.ast.SqlDatabaseType;
import zserio.extension.common.ZserioExtensionException;
import zserio.tools.ExtensionParameters;

class SqlDatabaseEmitter extends PythonDefaultEmitter
{
    public SqlDatabaseEmitter(String outputPath, ExtensionParameters extensionParameters)
    {
        super(outputPath, extensionParameters);
    }

    @Override
    public void beginSqlDatabase(SqlDatabaseType sqlDatabaseType) throws ZserioExtensionException
    {
        if (getWithSqlCode())
        {
            final Object templateData = new SqlDatabaseEmitterTemplateData(getTemplateDataContext(),
                    sqlDatabaseType);
            processSourceTemplate(TEMPLATE_SOURCE_NAME, templateData, sqlDatabaseType);
        }
    }

    private static final String TEMPLATE_SOURCE_NAME = "SqlDatabase.py.ftl";
}
