package zserio.emit.python;

import zserio.ast.PubsubType;
import zserio.emit.common.ZserioEmitException;
import zserio.tools.Parameters;

class PubsubEmitter extends PythonDefaultEmitter
{
    public PubsubEmitter(String outputPath, Parameters extensionParameters)
    {
        super(outputPath, extensionParameters);
    }

    @Override
    public void beginPubsub(PubsubType pubsubType) throws ZserioEmitException
    {
        if (!getWithPubsubCode())
            return;

        final PubsubEmitterTemplateData templateData = new PubsubEmitterTemplateData(
                getTemplateDataContext(), pubsubType);
        processSourceTemplate(TEMPLATE_NAME, templateData, pubsubType);
    }

    private static final String TEMPLATE_NAME = "Pubsub.py.ftl";
}
