package zserio.extension.java;

import zserio.ast.Constant;
import zserio.extension.common.ZserioExtensionException;

class ConstEmitter extends JavaDefaultEmitter
{
    public ConstEmitter(JavaExtensionParameters javaParameters)
    {
        super(javaParameters);
    }

    @Override
    public void beginConst(Constant constant) throws ZserioExtensionException
    {
        final ConstEmitterTemplateData templateData =
                new ConstEmitterTemplateData(getTemplateDataContext(), constant);
        processTemplate(TEMPLATE_NAME, templateData, constant.getPackage(), constant.getName());
    }

    private static final String TEMPLATE_NAME = "Constant.java.ftl";
}
