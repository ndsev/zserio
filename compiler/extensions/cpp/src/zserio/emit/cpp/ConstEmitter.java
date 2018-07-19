package zserio.emit.cpp;

import antlr.collections.AST;
import zserio.ast.ConstType;
import zserio.tools.Parameters;

public class ConstEmitter extends CppDefaultEmitter
{
    public ConstEmitter(String outPathName, Parameters extensionParameters)
    {
        super(outPathName, extensionParameters);
        templateData = new ConstEmitterTemplateData(getTemplateDataContext());
    }

    @Override
    public void beginConst(AST token) throws ZserioEmitCppException
    {
        if (!(token instanceof ConstType))
            throw new ZserioEmitCppException("Unexpected token type in beginConst!");

        final ConstType constType = (ConstType)token;
        templateData.add(constType);
    }

    @Override
    public void endRoot() throws ZserioEmitCppException
    {
        if (!templateData.isEmpty())
            processHeaderTemplateToRootDir(TEMPLATE_HEADER_NAME, templateData, OUTPUT_FILE_NAME_ROOT);
    }

    private final ConstEmitterTemplateData templateData;

    private static final String TEMPLATE_HEADER_NAME = "ConstType.h.ftl";
    private static final String OUTPUT_FILE_NAME_ROOT = "ConstType";
}
