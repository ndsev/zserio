package zserio.extension.python;

import zserio.ast.ChoiceType;
import zserio.extension.common.OutputFileManager;
import zserio.extension.common.ZserioExtensionException;

class ChoiceEmitter extends PythonDefaultEmitter
{
    public ChoiceEmitter(OutputFileManager outputFileManager, PythonExtensionParameters pythonParameters)
    {
        super(outputFileManager, pythonParameters);
    }

    @Override
    public void beginChoice(ChoiceType choiceType) throws ZserioExtensionException
    {
        final ChoiceEmitterTemplateData templateData =
                new ChoiceEmitterTemplateData(getTemplateDataContext(), choiceType);
        processSourceTemplate(TEMPLATE_SOURCE_NAME, templateData, choiceType);
    }

    static final String TEMPLATE_SOURCE_NAME = "Choice.py.ftl";
}
