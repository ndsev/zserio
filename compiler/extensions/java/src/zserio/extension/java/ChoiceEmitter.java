package zserio.extension.java;

import zserio.ast.ChoiceType;
import zserio.extension.common.OutputFileManager;
import zserio.extension.common.PackedTypesCollector;
import zserio.extension.common.ZserioExtensionException;

/**
 * Choice emitter.
 */
final class ChoiceEmitter extends JavaDefaultEmitter
{
    public ChoiceEmitter(OutputFileManager outputFileManager, JavaExtensionParameters javaParameters,
            PackedTypesCollector packedTypesCollector)
    {
        super(outputFileManager, javaParameters, packedTypesCollector);
    }

    @Override
    public void beginChoice(ChoiceType choiceType) throws ZserioExtensionException
    {
        final Object templateData = new ChoiceEmitterTemplateData(getTemplateDataContext(), choiceType);
        processTemplate(TEMPLATE_NAME, templateData, choiceType);
    }

    private static final String TEMPLATE_NAME = "Choice.java.ftl";
}
