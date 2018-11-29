package zserio.emit.python;

import zserio.ast.UnionType;
import zserio.emit.common.ZserioEmitException;
import zserio.tools.Parameters;

public class UnionEmitter extends PythonDefaultEmitter
{
    public UnionEmitter(String outputPath, Parameters extensionParameters)
    {
        super(outputPath, extensionParameters);
    }

    @Override
    public void beginUnion(UnionType enumType) throws ZserioEmitException
    {
        final Object templateData = new UnionEmitterTemplateData(getTemplateDataContext(), enumType);
        processSourceTemplate(TEMPLATE_SOURCE_NAME, templateData, enumType);
    }

    private static final String TEMPLATE_SOURCE_NAME = "Union.py.ftl";
}