package zserio.emit.cpp;

import antlr.collections.AST;
import zserio.ast.SqlDatabaseType;
import zserio.tools.Parameters;

public class MasterDatabaseEmitter extends CppDefaultEmitter
{
    public MasterDatabaseEmitter(String outPathName, Parameters extensionParameters)
    {
        super(outPathName, extensionParameters);
        templateData = new MasterDatabaseTemplateData(getTemplateDataContext());
    }

    @Override
    public void beginSqlDatabase(AST token) throws ZserioEmitCppException
    {
        if (!(token instanceof SqlDatabaseType))
            throw new ZserioEmitCppException("Unexpected token type in beginSqlDatabase!");

        if (getWithSqlCode())
        {
            final SqlDatabaseType databaseType = (SqlDatabaseType)token;
            templateData.add(databaseType);
        }
    }

    @Override
    public void endRoot() throws ZserioEmitCppException
    {
        if (getWithSqlCode() && !templateData.isEmpty())
        {
            processHeaderTemplateToRootDir(TEMPLATE_HEADER_NAME, templateData, OUTPUT_FILE_NAME_ROOT);
            processSourceTemplateToRootDir(TEMPLATE_SOURCE_NAME, templateData, OUTPUT_FILE_NAME_ROOT);
        }
    }

    private final MasterDatabaseTemplateData templateData;

    private static final String TEMPLATE_SOURCE_NAME = "MasterDatabase.cpp.ftl";
    private static final String TEMPLATE_HEADER_NAME = "MasterDatabase.h.ftl";
    private static final String OUTPUT_FILE_NAME_ROOT = "MasterDatabase";
}
