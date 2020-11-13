package zserio.extension.python;

import zserio.ast.PubsubType;
import zserio.extension.common.ZserioExtensionException;
import zserio.tools.ExtensionParameters;

class PubsubEmitter extends PythonDefaultEmitter
{
    public PubsubEmitter(String outputPath, ExtensionParameters extensionParameters)
    {
        super(outputPath, extensionParameters);
    }

    @Override
    public void beginPubsub(PubsubType pubsubType) throws ZserioExtensionException
    {
        if (!getWithPubsubCode())
            return;

        final PubsubEmitterTemplateData templateData = new PubsubEmitterTemplateData(
                getTemplateDataContext(), pubsubType);
        processSourceTemplate(TEMPLATE_NAME, templateData, pubsubType);
    }

    private static final String TEMPLATE_NAME = "Pubsub.py.ftl";
}
