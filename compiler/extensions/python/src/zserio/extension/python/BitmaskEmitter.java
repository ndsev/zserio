package zserio.extension.python;

import zserio.ast.BitmaskType;
import zserio.extension.common.ZserioExtensionException;

public class BitmaskEmitter extends PythonDefaultEmitter
{
    public BitmaskEmitter(PythonExtensionParameters pythonParameters)
    {
        super(pythonParameters);
    }

    @Override
    public void beginBitmask(BitmaskType bitmaskType) throws ZserioExtensionException
    {
        final Object templateData = new BitmaskEmitterTemplateData(getTemplateDataContext(), bitmaskType);
        processSourceTemplate(TEMPLATE_SOURCE_NAME, templateData, bitmaskType);
    }

    private static final String TEMPLATE_SOURCE_NAME = "Bitmask.py.ftl";
}
