package zserio.extension.python;

import zserio.ast.Constant;
import zserio.extension.common.ZserioExtensionException;

public class ConstEmitter extends PythonDefaultEmitter
{
    public ConstEmitter(PythonExtensionParameters pythonParameters)
    {
        super(pythonParameters);
    }

    @Override
    public void beginConst(Constant constant) throws ZserioExtensionException
    {
        final Object templateData = new ConstEmitterTemplateData(getTemplateDataContext(), constant);
        processSourceTemplate(TEMPLATE_SOURCE_NAME, templateData, constant.getPackage(), constant.getName());
    }

    private static final String TEMPLATE_SOURCE_NAME = "Constant.py.ftl";
}
