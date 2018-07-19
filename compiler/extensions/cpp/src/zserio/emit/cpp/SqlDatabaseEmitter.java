package zserio.emit.cpp;

import antlr.collections.AST;
import zserio.ast.SqlDatabaseType;
import zserio.tools.Parameters;

public class SqlDatabaseEmitter extends CppDefaultEmitter
{
    public SqlDatabaseEmitter(String outPathName, Parameters extensionParameters)
    {
        super(outPathName, extensionParameters);
    }

    @Override
    public void beginSqlDatabase(AST token) throws ZserioEmitCppException
    {
        if (!(token instanceof SqlDatabaseType))
            throw new ZserioEmitCppException("Unexpected token type in beginSqlDatabase!");

        if (getWithSqlCode())
        {
            final SqlDatabaseType databaseType = (SqlDatabaseType)token;
            final Object templateData = new SqlDatabaseEmitterTemplateData(getTemplateDataContext(),
                    databaseType);

            processHeaderTemplate(TEMPLATE_HEADER_NAME, templateData, databaseType);
            processSourceTemplate(TEMPLATE_SOURCE_NAME, templateData, databaseType);
        }
    }

    private static final String TEMPLATE_SOURCE_NAME = "SqlDatabase.cpp.ftl";
    private static final String TEMPLATE_HEADER_NAME = "SqlDatabase.h.ftl";
}
