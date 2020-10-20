package zserio.emit.java;

import zserio.ast.Constant;
import zserio.emit.common.ZserioEmitException;
import zserio.tools.Parameters;

class ConstEmitter extends JavaDefaultEmitter
{
    public ConstEmitter(JavaExtensionParameters javaParameters, Parameters extensionParameters)
    {
        super(javaParameters, extensionParameters);
    }

    @Override
    public void beginConst(Constant constant) throws ZserioEmitException
    {
        final ConstEmitterTemplateData templateData =
                new ConstEmitterTemplateData(getTemplateDataContext(), constant);
        processTemplate(TEMPLATE_NAME, templateData, constant.getPackage(), constant.getName());
    }

    private static final String TEMPLATE_NAME = "Constant.java.ftl";
}
