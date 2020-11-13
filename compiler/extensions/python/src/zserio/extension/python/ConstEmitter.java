package zserio.extension.python;

import zserio.ast.Constant;
import zserio.extension.common.ZserioExtensionException;
import zserio.tools.ExtensionParameters;

public class ConstEmitter extends PythonDefaultEmitter
{
    public ConstEmitter(String outputPath, ExtensionParameters extensionParameters)
    {
        super(outputPath, extensionParameters);
    }

    @Override
    public void beginConst(Constant constant) throws ZserioExtensionException
    {
        final Object templateData = new ConstEmitterTemplateData(getTemplateDataContext(), constant);
        processSourceTemplate(TEMPLATE_SOURCE_NAME, templateData, constant.getPackage(), constant.getName());
    }

    private static final String TEMPLATE_SOURCE_NAME = "Constant.py.ftl";
}
