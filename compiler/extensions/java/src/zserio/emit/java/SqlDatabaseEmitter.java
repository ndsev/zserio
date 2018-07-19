package zserio.emit.java;

import antlr.collections.AST;
import zserio.ast.SqlDatabaseType;
import zserio.tools.Parameters;

class SqlDatabaseEmitter extends JavaDefaultEmitter
{
    public SqlDatabaseEmitter(Parameters extensionParameters, JavaExtensionParameters javaParameters)
    {
        super(extensionParameters, javaParameters);
    }

    /** {@inheritDoc} */
    @Override
    public void beginSqlDatabase(AST token) throws ZserioEmitJavaException
    {
        if (!(token instanceof SqlDatabaseType))
            throw new ZserioEmitJavaException("Unexpected token type in beginSqlDatabase!");

        if (getWithSqlCode())
        {
            final SqlDatabaseType databaseType = (SqlDatabaseType)token;
            final Object templateData = new SqlDatabaseEmitterTemplateData(getTemplateDataContext(),
                    databaseType);
            processTemplate(TEMPLATE_NAME, templateData, databaseType);
        }
    }

    private static final String TEMPLATE_NAME = "SqlDatabase.java.ftl";
}
