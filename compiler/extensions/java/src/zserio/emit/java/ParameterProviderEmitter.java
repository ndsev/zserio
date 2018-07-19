package zserio.emit.java;

import antlr.collections.AST;
import zserio.ast.SqlTableType;
import zserio.tools.Parameters;

class ParameterProviderEmitter extends JavaDefaultEmitter
{
    public ParameterProviderEmitter(Parameters extensionParameters, JavaExtensionParameters javaParameters)
    {
        super(extensionParameters, javaParameters);
        parameterTemplateData = (getWithSqlCode()) ?
                new ParameterProviderTemplateData(getTemplateDataContext()) : null;
        generateParameterProvider = false;
    }

    /** {@inheritDoc} */
    @Override
    public void beginSqlTable(AST token) throws ZserioEmitJavaException
    {
        if (!(token instanceof SqlTableType))
            throw new ZserioEmitJavaException("Unexpected token type in beginSqlTable!");

        if (getWithSqlCode())
        {
            final SqlTableType tableType = (SqlTableType)token;
            parameterTemplateData.add(tableType);
            generateParameterProvider = true;
        }
    }

    @Override
    public void endRoot() throws ZserioEmitJavaException
    {
        if (generateParameterProvider)
        {
            processTemplateToRootDir(PARAMETER_TEMPLATE_NAME, parameterTemplateData,
                    PARAMETER_OUTPUT_FILE_NAME);
        }
    }

    private static final String PARAMETER_TEMPLATE_NAME = "IParameterProvider.java.ftl";
    private static final String PARAMETER_OUTPUT_FILE_NAME = "IParameterProvider";

    private final ParameterProviderTemplateData parameterTemplateData;
    private boolean generateParameterProvider;
}
