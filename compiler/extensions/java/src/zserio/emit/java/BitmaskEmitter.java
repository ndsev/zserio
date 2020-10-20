package zserio.emit.java;

import zserio.ast.BitmaskType;
import zserio.emit.common.ZserioEmitException;
import zserio.tools.Parameters;

class BitmaskEmitter extends JavaDefaultEmitter
{
    public BitmaskEmitter(JavaExtensionParameters javaParameters, Parameters extensionParameters)
    {
        super(javaParameters, extensionParameters);
    }

    @Override
    public void beginBitmask(BitmaskType bitmaskType) throws ZserioEmitException
    {
        final Object templateData = new BitmaskEmitterTemplateData(getTemplateDataContext(), bitmaskType);
        processTemplate(TEMPLATE_NAME, templateData, bitmaskType);
    }

    private static final String TEMPLATE_NAME = "Bitmask.java.ftl";
}
