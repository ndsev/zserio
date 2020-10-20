package zserio.emit.java;

import zserio.ast.ChoiceType;
import zserio.emit.common.ZserioEmitException;
import zserio.tools.Parameters;

class ChoiceEmitter extends JavaDefaultEmitter
{
    public ChoiceEmitter(JavaExtensionParameters javaParameters, Parameters extensionParameters)
    {
        super(javaParameters, extensionParameters);
    }

    @Override
    public void beginChoice(ChoiceType choiceType) throws ZserioEmitException
    {
        final Object templateData = new ChoiceEmitterTemplateData(getTemplateDataContext(), choiceType);
        processTemplate(TEMPLATE_NAME, templateData, choiceType);
    }

    private static final String TEMPLATE_NAME = "Choice.java.ftl";
}
