package zserio.emit.java;

import antlr.collections.AST;
import zserio.ast.ConstType;
import zserio.tools.Parameters;

class ConstEmitter extends JavaDefaultEmitter
{
    public ConstEmitter(Parameters extensionParameters, JavaExtensionParameters javaParameters)
    {
        super(extensionParameters, javaParameters);
        templateData = new ConstEmitterTemplateData(getTemplateDataContext());
    }

    /** {@inheritDoc} */
    @Override
    public void beginConst(AST token) throws ZserioEmitJavaException
    {
        if (!(token instanceof ConstType))
            throw new ZserioEmitJavaException("Unexpected token type in beginConst!");

        final ConstType constType = (ConstType)token;
        templateData.add(constType);
    }

    /** {@inheritDoc} */
    @Override
    public void endRoot() throws ZserioEmitJavaException
    {
        if (!templateData.isEmpty())
            processTemplateToRootDir(TEMPLATE_NAME, templateData, CONST_CLASS_FILE_NAME);
    }

    private static final String TEMPLATE_NAME = "ConstType.java.ftl";
    private static final String CONST_CLASS_FILE_NAME = "__ConstType";

    private final ConstEmitterTemplateData templateData;
}
