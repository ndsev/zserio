package zserio.extension.java;

import zserio.ast.BitmaskType;
import zserio.extension.common.OutputFileManager;
import zserio.extension.common.ZserioExtensionException;

/**
 * Bitmask emitter.
 */
class BitmaskEmitter extends JavaDefaultEmitter
{
    public BitmaskEmitter(OutputFileManager outputFileManager, JavaExtensionParameters javaParameters)
    {
        super(outputFileManager, javaParameters);
    }

    @Override
    public void beginBitmask(BitmaskType bitmaskType) throws ZserioExtensionException
    {
        final Object templateData = new BitmaskEmitterTemplateData(getTemplateDataContext(), bitmaskType);
        processTemplate(TEMPLATE_NAME, templateData, bitmaskType);
    }

    private static final String TEMPLATE_NAME = "Bitmask.java.ftl";
}
