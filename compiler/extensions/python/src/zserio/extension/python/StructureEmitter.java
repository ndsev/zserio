package zserio.extension.python;

import zserio.ast.StructureType;
import zserio.extension.common.ZserioExtensionException;
import zserio.tools.ExtensionParameters;

public class StructureEmitter extends PythonDefaultEmitter
{
    public StructureEmitter(String outputPath, ExtensionParameters extensionParameters)
    {
        super(outputPath, extensionParameters);
    }

    @Override
    public void beginStructure(StructureType structureType) throws ZserioExtensionException
    {
        final Object templateData = new StructureEmitterTemplateData(getTemplateDataContext(), structureType);
        processSourceTemplate(TEMPLATE_SOURCE_NAME, templateData, structureType);
    }

    private static final String TEMPLATE_SOURCE_NAME = "Structure.py.ftl";
}
