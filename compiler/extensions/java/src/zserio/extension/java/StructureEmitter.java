package zserio.extension.java;

import zserio.ast.StructureType;
import zserio.extension.common.ZserioExtensionException;

class StructureEmitter extends JavaDefaultEmitter
{
    public StructureEmitter(JavaExtensionParameters javaParameters)
    {
        super(javaParameters);
    }

    @Override
    public void beginStructure(StructureType structureType) throws ZserioExtensionException
    {
        Object templateData = new StructureEmitterTemplateData(getTemplateDataContext(), structureType);
        processTemplate(TEMPLATE_NAME, templateData, structureType);
    }

    private static final String TEMPLATE_NAME = "Structure.java.ftl";
}
