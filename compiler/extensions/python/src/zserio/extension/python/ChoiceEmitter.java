package zserio.extension.python;

import zserio.ast.ChoiceType;
import zserio.extension.common.ZserioExtensionException;
import zserio.tools.ExtensionParameters;

public class ChoiceEmitter extends PythonDefaultEmitter
{
    public ChoiceEmitter(String outputPath, ExtensionParameters extensionParameters)
    {
        super(outputPath, extensionParameters);
    }

    @Override
    public void beginChoice(ChoiceType choiceType) throws ZserioExtensionException
    {
        final Object templateData = new ChoiceEmitterTemplateData(getTemplateDataContext(), choiceType);
        processSourceTemplate(TEMPLATE_SOURCE_NAME, templateData, choiceType);
    }

    private static final String TEMPLATE_SOURCE_NAME = "Choice.py.ftl";
}
