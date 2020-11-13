package zserio.extension.python;

import zserio.ast.EnumType;
import zserio.extension.common.ZserioExtensionException;
import zserio.tools.ExtensionParameters;

public class EnumerationEmitter extends PythonDefaultEmitter
{
    public EnumerationEmitter(String outputPath, ExtensionParameters extensionParameters)
    {
        super(outputPath, extensionParameters);
    }

    @Override
    public void beginEnumeration(EnumType enumType) throws ZserioExtensionException
    {
        final Object templateData = new EnumerationEmitterTemplateData(getTemplateDataContext(), enumType);
        processSourceTemplate(TEMPLATE_SOURCE_NAME, templateData, enumType);
    }

    private static final String TEMPLATE_SOURCE_NAME = "Enumeration.py.ftl";
}
