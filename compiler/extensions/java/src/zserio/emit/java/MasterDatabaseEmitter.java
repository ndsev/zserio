package zserio.emit.java;

import antlr.collections.AST;
import zserio.ast.SqlDatabaseType;
import zserio.tools.Parameters;

class MasterDatabaseEmitter extends JavaDefaultEmitter
{
    public MasterDatabaseEmitter(Parameters extensionParameters, JavaExtensionParameters javaParameters)
    {
        super(extensionParameters, javaParameters);
        templateData = new MasterDatabaseTemplateData(getTemplateDataContext());
    }

    @Override
    public void beginSqlDatabase(AST token) throws ZserioEmitJavaException
    {
        if (!(token instanceof SqlDatabaseType))
            throw new ZserioEmitJavaException("Unexpected token type in beginSqlDatabase!");

        if (getWithSqlCode())
        {
            final SqlDatabaseType databaseType = (SqlDatabaseType)token;
            templateData.add(databaseType);
        }
    }

    @Override
    public void endRoot() throws ZserioEmitJavaException
    {
        if (getWithSqlCode() && !templateData.isEmpty())
        {
            processTemplateToRootDir(TEMPLATE_NAME, templateData, OUTPUT_FILE_NAME);
        }
    }

    private final MasterDatabaseTemplateData templateData;

    private static final String TEMPLATE_NAME = "MasterDatabase.java.ftl";
    private static final String OUTPUT_FILE_NAME = "MasterDatabase";
}
