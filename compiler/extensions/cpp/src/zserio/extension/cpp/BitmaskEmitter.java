package zserio.extension.cpp;

import zserio.ast.BitmaskType;
import zserio.extension.common.ZserioExtensionException;
import zserio.tools.ExtensionParameters;

public class BitmaskEmitter extends CppDefaultEmitter
{
    public BitmaskEmitter(String outPathName, ExtensionParameters extensionParameters)
    {
        super(outPathName, extensionParameters);
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
