package zserio.emit.cpp;

import java.util.ArrayList;
import java.util.List;

import antlr.collections.AST;
import zserio.ast.SqlTableType;
import zserio.tools.Parameters;

class ParameterProviderEmitter extends CppDefaultEmitter
{
    public ParameterProviderEmitter(String outPathName, Parameters extensionParameters)
    {
        super(outPathName, extensionParameters);
    }

    /** {@inheritDoc} */
    @Override
    public void beginSqlTable(AST token) throws ZserioEmitCppException
    {
        if (!(token instanceof SqlTableType))
            throw new ZserioEmitCppException("Unexpected token type in beginSqlTable!");

        if (getWithSqlCode())
            sqlTableTypes.add((SqlTableType)token);
    }

    @Override
    public void endRoot() throws ZserioEmitCppException
    {
        if (!sqlTableTypes.isEmpty())
        {
            final ParameterProviderTemplateData templateData =
                    new ParameterProviderTemplateData(getTemplateDataContext(), sqlTableTypes);
            processHeaderTemplateToRootDir(TEMPLATE_HEADER_NAME, templateData, OUTPUT_FILE_NAME_ROOT);
        }
    }

    private static final String TEMPLATE_HEADER_NAME = "IParameterProvider.h.ftl";
    private static final String OUTPUT_FILE_NAME_ROOT = "IParameterProvider";

    private final List<SqlTableType> sqlTableTypes = new ArrayList<SqlTableType>();
}
