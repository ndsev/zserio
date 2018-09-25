package zserio.emit.java;

import java.util.ArrayList;
import java.util.List;

import antlr.collections.AST;
import zserio.ast.SqlTableType;
import zserio.tools.Parameters;

class ParameterProviderEmitter extends JavaDefaultEmitter
{
    public ParameterProviderEmitter(Parameters extensionParameters, JavaExtensionParameters javaParameters)
    {
        super(extensionParameters, javaParameters);
    }

    /** {@inheritDoc} */
    @Override
    public void beginSqlTable(AST token) throws ZserioEmitJavaException
    {
        if (!(token instanceof SqlTableType))
            throw new ZserioEmitJavaException("Unexpected token type in beginSqlTable!");

        if (getWithSqlCode())
            sqlTableTypes.add((SqlTableType)token);
    }

    @Override
    public void endRoot() throws ZserioEmitJavaException
    {
        if (!sqlTableTypes.isEmpty())
        {
            parameterTemplateData = new ParameterProviderTemplateData(getTemplateDataContext(), sqlTableTypes);
            processTemplateToRootDir(PARAMETER_TEMPLATE_NAME, parameterTemplateData,
                    PARAMETER_OUTPUT_FILE_NAME);
        }
    }

    private static final String PARAMETER_TEMPLATE_NAME = "IParameterProvider.java.ftl";
    private static final String PARAMETER_OUTPUT_FILE_NAME = "IParameterProvider";

    private final List<SqlTableType> sqlTableTypes = new ArrayList<SqlTableType>();
    private ParameterProviderTemplateData parameterTemplateData;
}
