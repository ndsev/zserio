package zserio.extension.cpp;

import zserio.ast.EnumType;
import zserio.extension.common.OutputFileManager;
import zserio.extension.common.PackedTypesCollector;
import zserio.extension.common.ZserioExtensionException;

/**
 * Enumeration emitter.
 */
public final class EnumerationEmitter extends CppDefaultEmitter
{
    public EnumerationEmitter(OutputFileManager outputFileManager, CppExtensionParameters cppParameters,
            PackedTypesCollector packedTypesCollector)
    {
        super(outputFileManager, cppParameters, packedTypesCollector);
    }

    @Override
    public void beginEnumeration(EnumType enumType) throws ZserioExtensionException
    {
        final Object templateData = new EnumerationEmitterTemplateData(getTemplateDataContext(), enumType);

        processHeaderTemplate(TEMPLATE_HEADER_NAME, templateData, enumType);
        processSourceTemplate(TEMPLATE_SOURCE_NAME, templateData, enumType);
    }

    private static final String TEMPLATE_SOURCE_NAME = "Enumeration.cpp.ftl";
    private static final String TEMPLATE_HEADER_NAME = "Enumeration.h.ftl";
}
