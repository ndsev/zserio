package zserio.emit.python;

import zserio.ast.BitmaskType;
import zserio.emit.common.ZserioEmitException;
import zserio.tools.Parameters;

public class BitmaskEmitter extends PythonDefaultEmitter
{
    public BitmaskEmitter(String outputPath, Parameters extensionParameters)
    {
        super(outputPath, extensionParameters);
    }

    @Override
    public void beginBitmask(BitmaskType bitmaskType) throws ZserioEmitException
    {
        final Object templateData = new BitmaskEmitterTemplateData(getTemplateDataContext(), bitmaskType);
        processSourceTemplate(TEMPLATE_SOURCE_NAME, templateData, bitmaskType);
    }

    private static final String TEMPLATE_SOURCE_NAME = "Bitmask.py.ftl";
}