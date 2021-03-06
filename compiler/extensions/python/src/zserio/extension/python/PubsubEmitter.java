package zserio.extension.python;

import zserio.ast.PubsubType;
import zserio.extension.common.OutputFileManager;
import zserio.extension.common.ZserioExtensionException;

/**
 * Pubsub emitter.
 */
class PubsubEmitter extends PythonDefaultEmitter
{
    public PubsubEmitter(OutputFileManager outputFileManager, PythonExtensionParameters pythonParameters)
    {
        super(outputFileManager, pythonParameters);
    }

    @Override
    public void beginPubsub(PubsubType pubsubType) throws ZserioExtensionException
    {
        if (!getWithPubsubCode())
            return;

        final PubsubEmitterTemplateData templateData =
                new PubsubEmitterTemplateData(getTemplateDataContext(), pubsubType);
        processSourceTemplate(TEMPLATE_NAME, templateData, pubsubType);
    }

    private static final String TEMPLATE_NAME = "Pubsub.py.ftl";
}
