package zserio.emit.cpp;

import antlr.collections.AST;
import zserio.ast.UnionType;
import zserio.tools.Parameters;

public class UnionEmitter extends CppDefaultEmitter
{
    public UnionEmitter(String outPathName, Parameters extensionParameters)
    {
        super(outPathName, extensionParameters);
    }

    @Override
    public void beginUnion(AST token) throws ZserioEmitCppException
    {
        if (!(token instanceof UnionType))
            throw new ZserioEmitCppException("Unexpected token type in beginUnion!");

        final UnionType unionType = (UnionType)token;
        final Object templateData = new UnionEmitterTemplateData(getTemplateDataContext(), unionType);

        processHeaderTemplate(TEMPLATE_HEADER_NAME, templateData, unionType);
        processSourceTemplate(TEMPLATE_SOURCE_NAME, templateData, unionType);
    }

    @Override
    public void endRoot() throws ZserioEmitCppException
    {
    }

    private static final String TEMPLATE_HEADER_NAME = "Union.h.ftl";
    private static final String TEMPLATE_SOURCE_NAME = "Union.cpp.ftl";
}
