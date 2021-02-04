package zserio.extension.java;

import zserio.ast.SqlDatabaseType;
import zserio.extension.common.ZserioExtensionException;

class SqlDatabaseEmitter extends JavaDefaultEmitter
{
    public SqlDatabaseEmitter(JavaExtensionParameters javaParameters)
    {
        super(javaParameters);
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
