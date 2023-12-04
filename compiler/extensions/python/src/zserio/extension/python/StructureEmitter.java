package zserio.extension.python;

import zserio.ast.StructureType;
import zserio.extension.common.OutputFileManager;
import zserio.extension.common.PackedTypesCollector;
import zserio.extension.common.ZserioExtensionException;

/**
 * Structure emitter.
 */
final class StructureEmitter extends PythonDefaultEmitter
{
    public StructureEmitter(OutputFileManager outputFileManager, PythonExtensionParameters pythonParameters,
            PackedTypesCollector packedTypesCollector)
    {
        super(outputFileManager, pythonParameters,
                new StructTemplateDataContext(pythonParameters, packedTypesCollector));
    }

    @Override
    public void beginStructure(StructureType structureType) throws ZserioExtensionException
    {
        final StructureEmitterTemplateData templateData =
                new StructureEmitterTemplateData(getTemplateDataContext(), structureType);
        processSourceTemplate(TEMPLATE_SOURCE_NAME, templateData, structureType);
    }

    static final String TEMPLATE_SOURCE_NAME = "Structure.py.ftl";
}
