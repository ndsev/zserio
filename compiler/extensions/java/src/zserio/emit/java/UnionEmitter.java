package zserio.emit.java;

import antlr.collections.AST;
import zserio.ast.UnionType;
import zserio.tools.Parameters;

class UnionEmitter extends JavaDefaultEmitter
{
    public UnionEmitter(Parameters extensionParameters, JavaExtensionParameters javaParameters)
    {
        super(extensionParameters, javaParameters);
    }

    /** {@inheritDoc} */
    @Override
    public void beginUnion(AST token) throws ZserioEmitJavaException
    {
        if (!(token instanceof UnionType))
            throw new ZserioEmitJavaException("Unexpected token type in beginUnion!");

        final UnionType unionType = (UnionType)token;
        Object templateData = new UnionEmitterTemplateData(getTemplateDataContext(), unionType);
        processTemplate(TEMPLATE_NAME, templateData, unionType);
    }

    private static final String TEMPLATE_NAME = "Union.java.ftl";
}
