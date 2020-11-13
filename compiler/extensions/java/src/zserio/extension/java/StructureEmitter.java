package zserio.extension.java;

import zserio.ast.StructureType;
import zserio.extension.common.ZserioExtensionException;
import zserio.tools.ExtensionParameters;

class StructureEmitter extends JavaDefaultEmitter
{
    public StructureEmitter(JavaExtensionParameters javaParameters, ExtensionParameters extensionParameters)
    {
        super(javaParameters, extensionParameters);
    }

    @Override
    public void beginStructure(StructureType structureType) throws ZserioExtensionException
    {
        Object templateData = new StructureEmitterTemplateData(getTemplateDataContext(), structureType);
        processTemplate(TEMPLATE_NAME, templateData, structureType);
    }

    private static final String TEMPLATE_NAME = "Structure.java.ftl";
}
