package zserio.emit.java;

import zserio.ast.PubsubType;
import zserio.emit.common.ZserioEmitException;
import zserio.tools.Parameters;

class PubsubEmitter extends JavaDefaultEmitter
{
    public PubsubEmitter(JavaExtensionParameters javaParameters, Parameters extensionParameters)
    {
        super(javaParameters, extensionParameters);
    }

    @Override
    public void beginPubsub(PubsubType pubsubType) throws ZserioEmitException
    {
        if (!getWithPubsubCode())
            return;

        final PubsubEmitterTemplateData templateData = new PubsubEmitterTemplateData(
                getTemplateDataContext(), pubsubType);
        processTemplate(TEMPLATE_NAME, templateData, pubsubType);
    }

    private static final String TEMPLATE_NAME = "Pubsub.java.ftl";
}
