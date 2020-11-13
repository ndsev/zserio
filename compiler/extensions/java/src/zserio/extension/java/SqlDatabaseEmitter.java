package zserio.extension.java;

import zserio.ast.SqlDatabaseType;
import zserio.extension.common.ZserioExtensionException;
import zserio.tools.ExtensionParameters;

class SqlDatabaseEmitter extends JavaDefaultEmitter
{
    public SqlDatabaseEmitter(JavaExtensionParameters javaParameters, ExtensionParameters extensionParameters)
    {
        super(javaParameters, extensionParameters);
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
