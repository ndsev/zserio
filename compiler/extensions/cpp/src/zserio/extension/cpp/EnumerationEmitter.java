package zserio.extension.cpp;

import zserio.ast.EnumType;
import zserio.extension.common.ZserioExtensionException;
import zserio.tools.ExtensionParameters;

public class EnumerationEmitter extends CppDefaultEmitter
{
    public EnumerationEmitter(String outPathName, ExtensionParameters extensionParameters)
    {
        super(outPathName, extensionParameters);
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
