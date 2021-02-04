package zserio.extension.cpp;

import zserio.ast.StructureType;
import zserio.extension.common.ZserioExtensionException;

public class StructureEmitter extends CppDefaultEmitter
{
    public StructureEmitter(CppExtensionParameters cppParameters)
    {
        super(cppParameters);
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
