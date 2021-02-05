package zserio.extension.java;

import zserio.ast.PubsubType;
import zserio.extension.common.OutputFileManager;
import zserio.extension.common.ZserioExtensionException;

class PubsubEmitter extends JavaDefaultEmitter
{
    public PubsubEmitter(OutputFileManager outputFileManager, JavaExtensionParameters javaParameters)
    {
        super(outputFileManager, javaParameters);
    }

    @Override
    public void beginPubsub(PubsubType pubsubType) throws ZserioExtensionException
    {
        if (!getWithPubsubCode())
            return;

        final PubsubEmitterTemplateData templateData = new PubsubEmitterTemplateData(
                getTemplateDataContext(), pubsubType);
        processTemplate(TEMPLATE_NAME, templateData, pubsubType);
    }

    private static final String TEMPLATE_NAME = "Pubsub.java.ftl";
}
