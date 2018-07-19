package zserio.emit.cpp;

import antlr.collections.AST;
import zserio.ast.Subtype;
import zserio.tools.Parameters;

public class SubtypeEmitter extends CppDefaultEmitter
{
    public SubtypeEmitter(String outPathName, Parameters extensionParameters)
    {
        super(outPathName, extensionParameters);
    }

    @Override
    public void beginSubtype(AST token) throws ZserioEmitCppException
    {
        if (!(token instanceof Subtype))
            throw new ZserioEmitCppException("Unexpected token type in beginSubtype!");

        final Subtype subType = (Subtype)token;
        Object templateData = new SubtypeEmitterTemplateData(getTemplateDataContext(), subType);

        processHeaderTemplate(TEMPLATE_HEADER_NAME, templateData, subType);
    }

    private static final String TEMPLATE_HEADER_NAME = "Subtype.h.ftl";
}
