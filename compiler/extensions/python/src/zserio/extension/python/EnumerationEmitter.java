package zserio.extension.python;

import zserio.ast.EnumType;
import zserio.extension.common.OutputFileManager;
import zserio.extension.common.ZserioExtensionException;

class EnumerationEmitter extends PythonDefaultEmitter
{
    public EnumerationEmitter(OutputFileManager outputFileManager, PythonExtensionParameters pythonParameters)
    {
        super(outputFileManager, pythonParameters);
    }

    @Override
    public void beginEnumeration(EnumType enumType) throws ZserioExtensionException
    {
        final Object templateData = new EnumerationEmitterTemplateData(getTemplateDataContext(), enumType);
        processSourceTemplate(TEMPLATE_SOURCE_NAME, templateData, enumType);
    }

    private static final String TEMPLATE_SOURCE_NAME = "Enumeration.py.ftl";
}
