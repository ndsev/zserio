package zserio.emit.python;

import zserio.ast.ConstType;
import zserio.emit.common.ZserioEmitException;
import zserio.tools.Parameters;

public class ConstEmitter extends PythonDefaultEmitter
{
    public ConstEmitter(String outputPath, Parameters extensionParameters)
    {
        super(outputPath, extensionParameters);
    }

    @Override
    public void beginConst(ConstType constType) throws ZserioEmitException
    {
        final Object templateData = new ConstEmitterTemplateData(getTemplateDataContext(), constType);
        processSourceTemplate(TEMPLATE_SOURCE_NAME, templateData, constType);
    }

    private static final String TEMPLATE_SOURCE_NAME = "Const.py.ftl";
}