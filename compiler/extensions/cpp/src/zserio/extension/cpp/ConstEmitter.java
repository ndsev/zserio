package zserio.extension.cpp;

import zserio.ast.Constant;
import zserio.extension.common.ZserioExtensionException;

public class ConstEmitter extends CppDefaultEmitter
{
    public ConstEmitter(CppExtensionParameters cppParameters)
    {
        super(cppParameters);
    }

    @Override
    public void beginConst(Constant constant) throws ZserioExtensionException
    {
        final ConstEmitterTemplateData templateData = new ConstEmitterTemplateData(
                getTemplateDataContext(), constant);
        processHeaderTemplate(TEMPLATE_HEADER_NAME, templateData, constant.getPackage(), constant.getName());
    }

    private static final String TEMPLATE_HEADER_NAME = "Constant.h.ftl";
}
