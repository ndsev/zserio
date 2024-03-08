package zserio.extension.java;

import zserio.ast.CompoundType;
import zserio.ast.Subtype;
import zserio.ast.ZserioType;
import zserio.extension.common.OutputFileManager;
import zserio.extension.common.PackedTypesCollector;
import zserio.extension.common.ZserioExtensionException;

/**
 * Emitter for subtypes.
 */
public final class SubtypeEmitter extends JavaDefaultEmitter
{
    public SubtypeEmitter(OutputFileManager outputFileManager, JavaExtensionParameters javaParameters,
            PackedTypesCollector packedTypesCollector)
    {
        super(outputFileManager, javaParameters, packedTypesCollector);
    }

    @Override
    public void beginSubtype(Subtype subtype) throws ZserioExtensionException
    {
        final ZserioType referencedType = subtype.getBaseTypeReference().getType();
        if (referencedType instanceof CompoundType)
        {
            // subtypes are generated only for compound types
            final SubtypeEmitterTemplateData templateData =
                    new SubtypeEmitterTemplateData(getTemplateDataContext(), subtype);
            processTemplate(TEMPLATE_NAME, templateData, subtype.getPackage(), subtype.getName());
        }
    }

    private static final String TEMPLATE_NAME = "Subtype.java.ftl";
}
