package zserio.emit.java;

import zserio.ast.StructureType;
import zserio.emit.common.ZserioEmitException;
import zserio.tools.Parameters;

class StructureEmitter extends JavaDefaultEmitter
{
    public StructureEmitter(Parameters extensionParameters, JavaExtensionParameters javaParameters)
    {
        super(extensionParameters, javaParameters);
    }

    @Override
    public void beginStructure(StructureType structureType) throws ZserioEmitException
    {
        Object templateData = new StructureEmitterTemplateData(getTemplateDataContext(), structureType);
        processTemplate(TEMPLATE_NAME, templateData, structureType);
    }

    private static final String TEMPLATE_NAME = "Structure.java.ftl";
}
