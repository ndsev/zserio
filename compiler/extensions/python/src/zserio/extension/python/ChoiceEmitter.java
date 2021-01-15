package zserio.extension.python;

import zserio.ast.ChoiceType;
import zserio.extension.common.ZserioExtensionException;

public class ChoiceEmitter extends CompoundEmitter
{
    public ChoiceEmitter(PythonExtensionParameters pythonParameters)
    {
        super(pythonParameters);
    }

    @Override
    public void beginChoice(ChoiceType choiceType) throws ZserioExtensionException
    {
        final ChoiceEmitterTemplateData templateData =
                new ChoiceEmitterTemplateData(getTemplateDataContext(), choiceType);
        processCompoundTemplate(TEMPLATE_SOURCE_NAME, templateData, choiceType);
    }

    private static final String TEMPLATE_SOURCE_NAME = "Choice.py.ftl";
}
