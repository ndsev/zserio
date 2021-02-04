package zserio.extension.java;

import zserio.ast.BitmaskType;
import zserio.extension.common.ZserioExtensionException;

class BitmaskEmitter extends JavaDefaultEmitter
{
    public BitmaskEmitter(JavaExtensionParameters javaParameters)
    {
        super(javaParameters);
    }

    @Override
    public void beginBitmask(BitmaskType bitmaskType) throws ZserioExtensionException
    {
        final Object templateData = new BitmaskEmitterTemplateData(getTemplateDataContext(), bitmaskType);
        processTemplate(TEMPLATE_NAME, templateData, bitmaskType);
    }

    private static final String TEMPLATE_NAME = "Bitmask.java.ftl";
}
