package zserio.extension.java;

import zserio.ast.Constant;
import zserio.extension.common.OutputFileManager;
import zserio.extension.common.ZserioExtensionException;

class ConstEmitter extends JavaDefaultEmitter
{
    public ConstEmitter(OutputFileManager outputFileManager, JavaExtensionParameters javaParameters)
    {
        super(outputFileManager, javaParameters);
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
