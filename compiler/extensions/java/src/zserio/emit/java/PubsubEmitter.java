package zserio.emit.java;

import zserio.ast.PubsubType;
import zserio.emit.common.ZserioEmitException;
import zserio.tools.Parameters;

class PubsubEmitter extends JavaDefaultEmitter
{
    public PubsubEmitter(Parameters extensionParameters, JavaExtensionParameters javaParameters)
    {
        super(extensionParameters, javaParameters);
    }

    @Override
    public void beginPubsub(PubsubType pubsubType) throws ZserioEmitException
    {
        if (!getWithServiceCode())
            return;

        final PubsubEmitterTemplateData templateData = new PubsubEmitterTemplateData(
                getTemplateDataContext(), pubsubType);
        processTemplate(TEMPLATE_NAME, templateData, pubsubType);
    }

    private static final String TEMPLATE_NAME = "Pubsub.java.ftl";
}
