package zserio.emit.java;

import antlr.collections.AST;
import zserio.ast.ChoiceType;
import zserio.tools.Parameters;

class ChoiceEmitter extends JavaDefaultEmitter
{
    public ChoiceEmitter(Parameters extensionParameters, JavaExtensionParameters javaParameters)
    {
        super(extensionParameters, javaParameters);
    }

    /** {@inheritDoc} */
    @Override
    public void beginChoice(AST token) throws ZserioEmitJavaException
    {
        if (!(token instanceof ChoiceType))
            throw new ZserioEmitJavaException("Unexpected token type in beginChoice!");

        final ChoiceType choiceType = (ChoiceType)token;
        final Object templateData = new ChoiceEmitterTemplateData(getTemplateDataContext(), choiceType);
        processTemplate(TEMPLATE_NAME, templateData, choiceType);
    }

    private static final String TEMPLATE_NAME = "Choice.java.ftl";
}
