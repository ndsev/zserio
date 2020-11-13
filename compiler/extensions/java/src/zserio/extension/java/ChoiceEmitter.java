package zserio.extension.java;

import zserio.ast.ChoiceType;
import zserio.extension.common.ZserioExtensionException;
import zserio.tools.ExtensionParameters;

class ChoiceEmitter extends JavaDefaultEmitter
{
    public ChoiceEmitter(JavaExtensionParameters javaParameters, ExtensionParameters extensionParameters)
    {
        super(javaParameters, extensionParameters);
    }

    @Override
    public void beginChoice(ChoiceType choiceType) throws ZserioExtensionException
    {
        final Object templateData = new ChoiceEmitterTemplateData(getTemplateDataContext(), choiceType);
        processTemplate(TEMPLATE_NAME, templateData, choiceType);
    }

    private static final String TEMPLATE_NAME = "Choice.java.ftl";
}
