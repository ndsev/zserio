package zserio.emit.cpp;

import antlr.collections.AST;
import zserio.ast.SqlTableType;
import zserio.tools.Parameters;

class ParameterProviderEmitter extends CppDefaultEmitter
{
    public ParameterProviderEmitter(String outPathName, Parameters extensionParameters)
    {
        super(outPathName, extensionParameters);
        templateData = getWithSqlCode() ? new ParameterProviderTemplateData(getTemplateDataContext()) : null;
        generateParameterProvider = false;
    }

    /** {@inheritDoc} */
    @Override
    public void beginSqlTable(AST token) throws ZserioEmitCppException
    {
        if (!(token instanceof SqlTableType))
            throw new ZserioEmitCppException("Unexpected token type in beginSqlTable!");

        if (templateData != null)
        {
            final SqlTableType tableType = (SqlTableType)token;
            templateData.add(tableType);
            generateParameterProvider = true;
        }
    }

    @Override
    public void endRoot() throws ZserioEmitCppException
    {
        if (generateParameterProvider)
        {
            processHeaderTemplateToRootDir(TEMPLATE_HEADER_NAME, templateData, OUTPUT_FILE_NAME_ROOT);
        }
    }

    private final ParameterProviderTemplateData templateData;
    private boolean generateParameterProvider;

    private static final String TEMPLATE_HEADER_NAME = "IParameterProvider.h.ftl";
    private static final String OUTPUT_FILE_NAME_ROOT = "IParameterProvider";
}
