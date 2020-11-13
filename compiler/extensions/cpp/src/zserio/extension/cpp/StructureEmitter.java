package zserio.extension.cpp;

import zserio.ast.StructureType;
import zserio.extension.common.ZserioExtensionException;
import zserio.tools.ExtensionParameters;

public class StructureEmitter extends CppDefaultEmitter
{
    public StructureEmitter(String outPathName, ExtensionParameters extensionParameters)
    {
        super(outPathName, extensionParameters);
    }

    @Override
    public void beginStructure(StructureType structureType) throws ZserioExtensionException
    {
        final Object templateData = new StructureEmitterTemplateData(getTemplateDataContext(), structureType);

        processHeaderTemplate(TEMPLATE_HEADER_NAME, templateData, structureType);
        processSourceTemplate(TEMPLATE_SOURCE_NAME, templateData, structureType);
    }

    private static final String TEMPLATE_SOURCE_NAME = "Structure.cpp.ftl";
    private static final String TEMPLATE_HEADER_NAME = "Structure.h.ftl";
}
