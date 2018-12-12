package zserio.emit.java;

import zserio.ast.ConstType;
import zserio.emit.common.ZserioEmitException;
import zserio.tools.Parameters;

class ConstEmitter extends JavaDefaultEmitter
{
    public ConstEmitter(Parameters extensionParameters, JavaExtensionParameters javaParameters)
    {
        super(extensionParameters, javaParameters);
    }

    @Override
    public void beginConst(ConstType constType) throws ZserioEmitException
    {
        final ConstEmitterTemplateData templateData =
                new ConstEmitterTemplateData(getTemplateDataContext(), constType);
        processTemplate(TEMPLATE_NAME, templateData, constType);
    }

    private static final String TEMPLATE_NAME = "ConstType.java.ftl";
}
