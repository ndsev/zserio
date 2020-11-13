package zserio.extension.python;

import zserio.ast.BitmaskType;
import zserio.extension.common.ZserioExtensionException;
import zserio.tools.ExtensionParameters;

public class BitmaskEmitter extends PythonDefaultEmitter
{
    public BitmaskEmitter(String outputPath, ExtensionParameters extensionParameters)
    {
        super(outputPath, extensionParameters);
    }

    @Override
    public void beginBitmask(BitmaskType bitmaskType) throws ZserioExtensionException
    {
        final Object templateData = new BitmaskEmitterTemplateData(getTemplateDataContext(), bitmaskType);
        processSourceTemplate(TEMPLATE_SOURCE_NAME, templateData, bitmaskType);
    }

    private static final String TEMPLATE_SOURCE_NAME = "Bitmask.py.ftl";
}
