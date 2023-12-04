package zserio.extension.python;

import zserio.ast.ChoiceType;
import zserio.extension.common.OutputFileManager;
import zserio.extension.common.PackedTypesCollector;
import zserio.extension.common.ZserioExtensionException;

/**
 * Choice emitter.
 */
final class ChoiceEmitter extends PythonDefaultEmitter
{
    public ChoiceEmitter(OutputFileManager outputFileManager, PythonExtensionParameters pythonParameters,
            PackedTypesCollector packedTypesCollector)
    {
        super(outputFileManager, pythonParameters,
                new ChoiceTemplateDataContext(pythonParameters, packedTypesCollector));
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
