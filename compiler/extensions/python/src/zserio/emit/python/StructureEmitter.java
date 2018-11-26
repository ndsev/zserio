package zserio.emit.python;

import zserio.ast.StructureType;
import zserio.emit.common.ZserioEmitException;
import zserio.tools.Parameters;

public class StructureEmitter extends PythonDefaultEmitter
{
    public StructureEmitter(String outputPath, Parameters extensionParameters)
    {
        super(outputPath, extensionParameters);
    }

    @Override
    public void beginStructure(StructureType structureType) throws ZserioEmitException
    {
        final Object templateData = new StructureEmitterTemplateData(getTemplateDataContext(), structureType);
        processSourceTemplate(TEMPLATE_SOURCE_NAME, templateData, structureType);
    }

    private static final String TEMPLATE_SOURCE_NAME = "Structure.py.ftl";
}
