package zserio.extension.python;

import zserio.ast.SqlDatabaseType;
import zserio.extension.common.ZserioExtensionException;

class SqlDatabaseEmitter extends CompoundEmitter
{
    public SqlDatabaseEmitter(PythonExtensionParameters pythonParameters)
    {
        super(pythonParameters);
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
