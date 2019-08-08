package zserio.emit.python;

import zserio.ast.SqlDatabaseType;
import zserio.emit.common.ZserioEmitException;
import zserio.tools.Parameters;

class SqlDatabaseEmitter extends PythonDefaultEmitter
{
    public SqlDatabaseEmitter(String outputPath, Parameters extensionParameters)
    {
        super(outputPath, extensionParameters);
    }

    @Override
    public void beginSqlDatabase(SqlDatabaseType sqlDatabaseType) throws ZserioEmitException
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
