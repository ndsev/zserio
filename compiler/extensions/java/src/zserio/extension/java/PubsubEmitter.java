package zserio.extension.java;

import zserio.ast.PubsubType;
import zserio.extension.common.ZserioExtensionException;
import zserio.tools.ExtensionParameters;

class PubsubEmitter extends JavaDefaultEmitter
{
    public PubsubEmitter(JavaExtensionParameters javaParameters, ExtensionParameters extensionParameters)
    {
        super(javaParameters, extensionParameters);
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
