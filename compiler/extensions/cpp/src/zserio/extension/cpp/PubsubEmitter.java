package zserio.extension.cpp;

import zserio.ast.PubsubType;
import zserio.extension.common.ZserioExtensionException;
import zserio.tools.ExtensionParameters;

public class PubsubEmitter extends CppDefaultEmitter
{
    public PubsubEmitter(String outPathName, ExtensionParameters extensionParameters)
    {
        super(outPathName, extensionParameters);
    }

    @Override
    public void beginPubsub(PubsubType pubsubType) throws ZserioExtensionException
    {
        if (!getWithPubsubCode())
            return;

        final TemplateDataContext templateDataContext = getTemplateDataContext();
        final PubsubEmitterTemplateData templateData =
                new PubsubEmitterTemplateData(templateDataContext, pubsubType);
        processSourceTemplate(TEMPLATE_SOURCE_NAME, templateData, pubsubType);
        processHeaderTemplate(TEMPLATE_HEADER_NAME, templateData, pubsubType);
    }

    private static final String TEMPLATE_SOURCE_NAME = "Pubsub.cpp.ftl";
    private static final String TEMPLATE_HEADER_NAME = "Pubsub.h.ftl";
}
