package zserio.extension.cpp;

import zserio.ast.BitmaskType;
import zserio.extension.common.OutputFileManager;
import zserio.extension.common.ZserioExtensionException;
import zserio.extension.cpp.TemplateDataContext.ContextParameters;

/**
 * Bitmask emitter.
 */
public final class BitmaskEmitter extends CppDefaultEmitter
{
    public BitmaskEmitter(OutputFileManager outputFileManager, CppExtensionParameters cppParameters,
            ContextParameters contextParameters)
    {
        super(outputFileManager, cppParameters, contextParameters);
    }

    @Override
    public void beginBitmask(BitmaskType bitmaskType) throws ZserioExtensionException
    {
        final Object templateData = new BitmaskEmitterTemplateData(getTemplateDataContext(), bitmaskType);

        processHeaderTemplate(TEMPLATE_HEADER_NAME, templateData, bitmaskType);
        processSourceTemplate(TEMPLATE_SOURCE_NAME, templateData, bitmaskType);
    }

    private static final String TEMPLATE_SOURCE_NAME = "Bitmask.cpp.ftl";
    private static final String TEMPLATE_HEADER_NAME = "Bitmask.h.ftl";
}
