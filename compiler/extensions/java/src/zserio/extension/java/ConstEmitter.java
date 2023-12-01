package zserio.extension.java;

import zserio.ast.Constant;
import zserio.extension.common.OutputFileManager;
import zserio.extension.common.PackedTypesCollector;
import zserio.extension.common.ZserioExtensionException;

/**
 * Emitter for constants.
 */
final class ConstEmitter extends JavaDefaultEmitter
{
    public ConstEmitter(OutputFileManager outputFileManager, JavaExtensionParameters javaParameters,
            PackedTypesCollector packedTypesCollector)
    {
        super(outputFileManager, javaParameters, packedTypesCollector);
    }

    @Override
    public void beginConst(Constant constant) throws ZserioExtensionException
    {
        final ConstEmitterTemplateData templateData =
                new ConstEmitterTemplateData(getTemplateDataContext(), constant);
        processTemplate(TEMPLATE_NAME, templateData, constant.getPackage(), constant.getName());
    }

    private static final String TEMPLATE_NAME = "Constant.java.ftl";
}
