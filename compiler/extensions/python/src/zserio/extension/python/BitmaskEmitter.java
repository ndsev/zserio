package zserio.extension.python;

import zserio.ast.BitmaskType;
import zserio.extension.common.OutputFileManager;
import zserio.extension.common.PackedTypesCollector;
import zserio.extension.common.ZserioExtensionException;

/**
 * Bitmask emitter.
 */
final class BitmaskEmitter extends PythonDefaultEmitter
{
    public BitmaskEmitter(OutputFileManager outputFileManager, PythonExtensionParameters pythonParameters,
            PackedTypesCollector packedTypesCollector)
    {
        super(outputFileManager, pythonParameters, packedTypesCollector);
    }

    @Override
    public void beginBitmask(BitmaskType bitmaskType) throws ZserioExtensionException
    {
        final BitmaskEmitterTemplateData templateData =
                new BitmaskEmitterTemplateData(getTemplateDataContext(), bitmaskType);
        processSourceTemplate(TEMPLATE_SOURCE_NAME, templateData, bitmaskType);
    }

    private static final String TEMPLATE_SOURCE_NAME = "Bitmask.py.ftl";
}
