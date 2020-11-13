package zserio.extension.java;

import zserio.ast.EnumType;
import zserio.extension.common.ZserioExtensionException;
import zserio.tools.ExtensionParameters;

class EnumerationEmitter extends JavaDefaultEmitter
{
    public EnumerationEmitter(JavaExtensionParameters javaParameters, ExtensionParameters extensionParameters)
    {
        super(javaParameters, extensionParameters);
    }

    @Override
    public void beginEnumeration(EnumType enumType) throws ZserioExtensionException
    {
        Object templateData = new EnumerationEmitterTemplateData(getTemplateDataContext(), enumType);
        processTemplate(TEMPLATE_NAME, templateData, enumType);
    }

    private static final String TEMPLATE_NAME = "Enumeration.java.ftl";
}
