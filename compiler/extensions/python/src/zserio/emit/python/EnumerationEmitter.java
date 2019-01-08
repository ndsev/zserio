package zserio.emit.python;

import zserio.ast.EnumType;
import zserio.emit.common.ZserioEmitException;
import zserio.tools.Parameters;

public class EnumerationEmitter extends PythonDefaultEmitter
{
    public EnumerationEmitter(String outputPath, Parameters extensionParameters)
    {
        super(outputPath, extensionParameters);
    }

    @Override
    public void beginEnumeration(EnumType enumType) throws ZserioEmitException
    {
        final Object templateData = new EnumerationEmitterTemplateData(getTemplateDataContext(), enumType);
        processSourceTemplate(TEMPLATE_SOURCE_NAME, templateData, enumType);
    }

    private static final String TEMPLATE_SOURCE_NAME = "Enumeration.py.ftl";
}