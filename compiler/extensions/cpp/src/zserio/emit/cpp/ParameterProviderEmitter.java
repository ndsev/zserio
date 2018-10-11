package zserio.emit.cpp;

import java.util.ArrayList;
import java.util.List;

import zserio.ast.Root;
import zserio.ast.SqlTableType;
import zserio.emit.common.ZserioEmitException;
import zserio.tools.Parameters;

class ParameterProviderEmitter extends CppDefaultEmitter
{
    public ParameterProviderEmitter(String outPathName, Parameters extensionParameters)
    {
        super(outPathName, extensionParameters);
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
            final ParameterProviderTemplateData templateData =
                    new ParameterProviderTemplateData(getTemplateDataContext(), sqlTableTypes);
            processHeaderTemplateToRootDir(TEMPLATE_HEADER_NAME, templateData, OUTPUT_FILE_NAME_ROOT);
        }
    }

    private static final String TEMPLATE_HEADER_NAME = "IParameterProvider.h.ftl";
    private static final String OUTPUT_FILE_NAME_ROOT = "IParameterProvider";

    private final List<SqlTableType> sqlTableTypes = new ArrayList<SqlTableType>();
}
