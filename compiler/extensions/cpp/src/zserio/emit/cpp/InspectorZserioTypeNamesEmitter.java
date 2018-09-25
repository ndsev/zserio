package zserio.emit.cpp;

import java.util.ArrayList;
import java.util.List;

import antlr.collections.AST;
import zserio.ast.EnumType;
import zserio.ast.Field;
import zserio.ast.FunctionType;
import zserio.tools.Parameters;

public class InspectorZserioTypeNamesEmitter extends CppDefaultEmitter
{
    public InspectorZserioTypeNamesEmitter(String outPathName, Parameters extensionParameters)
    {
        super(outPathName, extensionParameters);
    }

    @Override
    public void beginField(AST token) throws ZserioEmitCppException
    {
        if (!(token instanceof Field))
            throw new ZserioEmitCppException("Unexpected token type in beginField!");

        if (getWithInspectorCode())
            fields.add((Field)token);
    }

    @Override
    public void beginFunction(AST token) throws ZserioEmitCppException
    {
        if (!(token instanceof FunctionType))
            throw new ZserioEmitCppException("Unexpected token type in beginFunction!");

        if (getWithInspectorCode())
            functionTypes.add((FunctionType)token);
    }

    @Override
    public void beginEnumeration(AST token) throws ZserioEmitCppException
    {
        if (!(token instanceof EnumType))
            throw new ZserioEmitCppException("Unexpected token type in beginEnumeration!");

        if (getWithInspectorCode())
            enumTypes.add((EnumType)token);
    }

    @Override
    public void endRoot() throws ZserioEmitCppException
    {
        if (!fields.isEmpty() || !functionTypes.isEmpty() || !enumTypes.isEmpty())
        {
            final InspectorZserioTypeNamesTemplateData templateData =
                    new InspectorZserioTypeNamesTemplateData(getTemplateDataContext(), fields, functionTypes,
                            enumTypes);
            processHeaderTemplateToRootDir(TEMPLATE_HEADER_NAME, templateData, OUTPUT_FILE_NAME_ROOT);
            processSourceTemplateToRootDir(TEMPLATE_SOURCE_NAME, templateData, OUTPUT_FILE_NAME_ROOT);
        }
    }

    private static final String TEMPLATE_HEADER_NAME = "InspectorZserioTypeNames.h.ftl";
    private static final String TEMPLATE_SOURCE_NAME = "InspectorZserioTypeNames.cpp.ftl";
    private static final String OUTPUT_FILE_NAME_ROOT = "InspectorZserioTypeNames";

    private final List<Field> fields = new ArrayList<Field>();
    private final List<FunctionType> functionTypes = new ArrayList<FunctionType>();
    private final List<EnumType> enumTypes = new ArrayList<EnumType>();
}
