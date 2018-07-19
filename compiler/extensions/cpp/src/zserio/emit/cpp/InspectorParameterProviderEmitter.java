package zserio.emit.cpp;

import antlr.collections.AST;
import zserio.ast.SqlTableType;
import zserio.tools.Parameters;

public class InspectorParameterProviderEmitter extends CppDefaultEmitter
{
    public InspectorParameterProviderEmitter(String outPathName, Parameters extensionParameters)
    {
        super(outPathName, extensionParameters);

        templateData = (!getWithSqlCode() || !getWithInspectorCode()) ? null :
            new ParameterProviderTemplateData(getTemplateDataContext());
        generateInspectorParameterProvider = false;
    }

    @Override
    public void beginSqlTable(AST token) throws ZserioEmitCppException
    {
        if (!(token instanceof SqlTableType))
            throw new ZserioEmitCppException("Unexpected token type in beginSqlTable!");

        if (templateData != null)
        {
            final SqlTableType sqlTable = (SqlTableType)token;
            templateData.add(sqlTable);
            generateInspectorParameterProvider = true;
        }
    }

    @Override
    public void endRoot() throws ZserioEmitCppException
    {
        if (generateInspectorParameterProvider)
        {
            processHeaderTemplateToRootDir(IINSPECTOR_TEMPLATE_HEADER_NAME, templateData,
                    IINSPECTOR_OUTPUT_FILE_NAME_ROOT);

            processHeaderTemplateToRootDir(INSPECTOR_TEMPLATE_HEADER_NAME, templateData,
                    INSPECTOR_OUTPUT_FILE_NAME_ROOT);
            processSourceTemplateToRootDir(INSPECTOR_TEMPLATE_SOURCE_NAME, templateData,
                    INSPECTOR_OUTPUT_FILE_NAME_ROOT);
        }
    }

    private final ParameterProviderTemplateData templateData;
    private boolean generateInspectorParameterProvider;

    private static final String IINSPECTOR_TEMPLATE_HEADER_NAME = "IInspectorParameterProvider.h.ftl";
    private static final String IINSPECTOR_OUTPUT_FILE_NAME_ROOT = "IInspectorParameterProvider";

    private static final String INSPECTOR_TEMPLATE_HEADER_NAME =
            "InspectorParameterProvider.h.ftl";
    private static final String INSPECTOR_TEMPLATE_SOURCE_NAME =
            "InspectorParameterProvider.cpp.ftl";
    private static final String INSPECTOR_OUTPUT_FILE_NAME_ROOT =
            "InspectorParameterProvider";
}
