package zserio.emit.java;

import java.util.ArrayList;
import java.util.List;

import zserio.ast.Root;
import zserio.ast.SqlTableType;
import zserio.emit.common.ZserioEmitException;
import zserio.tools.Parameters;

class ParameterProviderEmitter extends JavaDefaultEmitter
{
    public ParameterProviderEmitter(Parameters extensionParameters, JavaExtensionParameters javaParameters)
    {
        super(extensionParameters, javaParameters);
    }

    @Override
    public void beginSqlTable(SqlTableType sqlTableType) throws ZserioEmitException
    {
        if (getWithSqlCode())
            sqlTableTypes.add(sqlTableType);
    }

    @Override
    public void endRoot(Root root) throws ZserioEmitException
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
