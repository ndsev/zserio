package zserio.emit.cpp;

import java.util.ArrayList;
import java.util.List;

import antlr.collections.AST;
import zserio.ast.ConstType;
import zserio.tools.Parameters;

public class ConstEmitter extends CppDefaultEmitter
{
    public ConstEmitter(String outPathName, Parameters extensionParameters)
    {
        super(outPathName, extensionParameters);
    }

    @Override
    public void beginConst(AST token) throws ZserioEmitCppException
    {
        if (!(token instanceof ConstType))
            throw new ZserioEmitCppException("Unexpected token type in beginConst!");

        constTypes.add((ConstType)token);
    }

    @Override
    public void endRoot() throws ZserioEmitCppException
    {
        if (!constTypes.isEmpty())
        {
            final ConstEmitterTemplateData templateData =
                    new ConstEmitterTemplateData(getTemplateDataContext(), constTypes);
            processHeaderTemplateToRootDir(TEMPLATE_HEADER_NAME, templateData, OUTPUT_FILE_NAME_ROOT);
        }
    }

    private static final String TEMPLATE_HEADER_NAME = "ConstType.h.ftl";
    private static final String OUTPUT_FILE_NAME_ROOT = "ConstType";

    private final List<ConstType> constTypes = new ArrayList<ConstType>();
}
