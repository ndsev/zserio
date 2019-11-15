package zserio.emit.cpp98;

import zserio.ast.Constant;
import zserio.emit.common.ZserioEmitException;
import zserio.tools.Parameters;

public class ConstEmitter extends CppDefaultEmitter
{
    public ConstEmitter(String outPathName, Parameters extensionParameters)
    {
        super(outPathName, extensionParameters);
    }

    @Override
    public void beginConst(Constant constant) throws ZserioEmitException
    {
        final ConstEmitterTemplateData templateData = new ConstEmitterTemplateData(getTemplateDataContext(),
                constant);
        processHeaderTemplate(TEMPLATE_HEADER_NAME, templateData, constant.getPackage().getPackageName(),
                constant.getName());
    }

    private static final String TEMPLATE_HEADER_NAME = "Constant.h.ftl";
}
