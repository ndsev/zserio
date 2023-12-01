package zserio.extension.java;

import zserio.ast.EnumType;
import zserio.extension.common.OutputFileManager;
import zserio.extension.common.PackedTypesCollector;
import zserio.extension.common.ZserioExtensionException;

/**
 * Enumeration emitter.
 */
final class EnumerationEmitter extends JavaDefaultEmitter
{
    public EnumerationEmitter(OutputFileManager outputFileManager, JavaExtensionParameters javaParameters,
        PackedTypesCollector packedTypesCollector)
    {
        super(outputFileManager, javaParameters, packedTypesCollector);
    }

    @Override
    public void beginEnumeration(EnumType enumType) throws ZserioExtensionException
    {
        Object templateData = new EnumerationEmitterTemplateData(getTemplateDataContext(), enumType);
        processTemplate(TEMPLATE_NAME, templateData, enumType);
    }

    private static final String TEMPLATE_NAME = "Enumeration.java.ftl";
}
