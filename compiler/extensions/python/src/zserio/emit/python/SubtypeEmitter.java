package zserio.emit.python;

import zserio.ast.Subtype;
import zserio.emit.common.ZserioEmitException;
import zserio.tools.Parameters;

public class SubtypeEmitter extends PythonDefaultEmitter
{
    public SubtypeEmitter(String outputPath, Parameters extensionParameters)
    {
        super(outputPath, extensionParameters);
    }

    @Override
    public void beginSubtype(Subtype subtype) throws ZserioEmitException
    {
        final Object templateData = new SubtypeEmitterTemplateData(getTemplateDataContext(), subtype);
        processSourceTemplate(TEMPLATE_SOURCE_NAME, templateData, subtype);
    }

    private static final String TEMPLATE_SOURCE_NAME = "Subtype.py.ftl";
}