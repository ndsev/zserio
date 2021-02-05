package zserio.extension.python;

import zserio.ast.StructureType;
import zserio.extension.common.OutputFileManager;
import zserio.extension.common.ZserioExtensionException;

public class StructureEmitter extends CompoundEmitter
{
    public StructureEmitter(OutputFileManager outputFileManager, PythonExtensionParameters pythonParameters)
    {
        super(outputFileManager, pythonParameters);
    }

    @Override
    public void beginStructure(StructureType structureType) throws ZserioExtensionException
    {
        final StructureEmitterTemplateData templateData =
                new StructureEmitterTemplateData(getTemplateDataContext(), structureType);
        processCompoundTemplate(TEMPLATE_SOURCE_NAME, templateData, structureType);
    }

    private static final String TEMPLATE_SOURCE_NAME = "Structure.py.ftl";
}
