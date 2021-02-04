package zserio.extension.java;

import zserio.ast.ChoiceType;
import zserio.extension.common.ZserioExtensionException;

class ChoiceEmitter extends JavaDefaultEmitter
{
    public ChoiceEmitter(JavaExtensionParameters javaParameters)
    {
        super(javaParameters);
    }

    @Override
    public void beginChoice(ChoiceType choiceType) throws ZserioExtensionException
    {
        final Object templateData = new ChoiceEmitterTemplateData(getTemplateDataContext(), choiceType);
        processTemplate(TEMPLATE_NAME, templateData, choiceType);
    }

    private static final String TEMPLATE_NAME = "Choice.java.ftl";
}
