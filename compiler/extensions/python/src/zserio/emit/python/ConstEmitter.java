package zserio.emit.python;

import zserio.ast.Constant;
import zserio.emit.common.ZserioEmitException;
import zserio.tools.Parameters;

public class ConstEmitter extends PythonDefaultEmitter
{
    public ConstEmitter(String outputPath, Parameters extensionParameters)
    {
        super(outputPath, extensionParameters);
    }

    @Override
    public void beginConst(Constant constant) throws ZserioEmitException
    {
        final Object templateData = new ConstEmitterTemplateData(getTemplateDataContext(), constant);
        processSourceTemplate(TEMPLATE_SOURCE_NAME, templateData, constant.getPackage().getPackageName(),
                constant.getName());
    }

    private static final String TEMPLATE_SOURCE_NAME = "Constant.py.ftl";
}