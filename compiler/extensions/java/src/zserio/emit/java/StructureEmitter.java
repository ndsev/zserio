package zserio.emit.java;

import zserio.ast.StructureType;
import zserio.emit.common.ZserioEmitException;
import zserio.tools.Parameters;

class StructureEmitter extends JavaDefaultEmitter
{
    public StructureEmitter(JavaExtensionParameters javaParameters, Parameters extensionParameters)
    {
        super(javaParameters, extensionParameters);
    }

    @Override
    public void beginStructure(StructureType structureType) throws ZserioEmitException
    {
        Object templateData = new StructureEmitterTemplateData(getTemplateDataContext(), structureType);
        processTemplate(TEMPLATE_NAME, templateData, structureType);
    }

    private static final String TEMPLATE_NAME = "Structure.java.ftl";
}
