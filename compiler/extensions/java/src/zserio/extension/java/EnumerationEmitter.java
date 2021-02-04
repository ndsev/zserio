package zserio.extension.java;

import zserio.ast.EnumType;
import zserio.extension.common.ZserioExtensionException;

class EnumerationEmitter extends JavaDefaultEmitter
{
    public EnumerationEmitter(JavaExtensionParameters javaParameters)
    {
        super(javaParameters);
    }

    @Override
    public void beginEnumeration(EnumType enumType) throws ZserioExtensionException
    {
        Object templateData = new EnumerationEmitterTemplateData(getTemplateDataContext(), enumType);
        processTemplate(TEMPLATE_NAME, templateData, enumType);
    }

    private static final String TEMPLATE_NAME = "Enumeration.java.ftl";
}
