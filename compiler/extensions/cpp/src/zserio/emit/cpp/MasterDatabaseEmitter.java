package zserio.emit.cpp;

import java.util.ArrayList;
import java.util.List;

import antlr.collections.AST;
import zserio.ast.SqlDatabaseType;
import zserio.tools.Parameters;

public class MasterDatabaseEmitter extends CppDefaultEmitter
{
    public MasterDatabaseEmitter(String outPathName, Parameters extensionParameters)
    {
        super(outPathName, extensionParameters);
    }

    @Override
    public void beginSqlDatabase(AST token) throws ZserioEmitCppException
    {
        if (!(token instanceof SqlDatabaseType))
            throw new ZserioEmitCppException("Unexpected token type in beginSqlDatabase!");

        if (getWithSqlCode())
            sqlDatabaseTypes.add((SqlDatabaseType)token);
    }

    @Override
    public void endRoot() throws ZserioEmitCppException
    {
        if (!sqlDatabaseTypes.isEmpty())
        {
            final MasterDatabaseTemplateData templateData =
                    new MasterDatabaseTemplateData(getTemplateDataContext(), sqlDatabaseTypes);
            processHeaderTemplateToRootDir(TEMPLATE_HEADER_NAME, templateData, OUTPUT_FILE_NAME_ROOT);
            processSourceTemplateToRootDir(TEMPLATE_SOURCE_NAME, templateData, OUTPUT_FILE_NAME_ROOT);
        }
    }

    private static final String TEMPLATE_SOURCE_NAME = "MasterDatabase.cpp.ftl";
    private static final String TEMPLATE_HEADER_NAME = "MasterDatabase.h.ftl";
    private static final String OUTPUT_FILE_NAME_ROOT = "MasterDatabase";

    private final List<SqlDatabaseType> sqlDatabaseTypes = new ArrayList<SqlDatabaseType>();
}
