package zserio.emit.cpp;

import zserio.ast.ConstType;
import zserio.emit.common.ZserioEmitException;
import zserio.tools.Parameters;

public class ConstEmitter extends CppDefaultEmitter
{
    public ConstEmitter(String outPathName, Parameters extensionParameters)
    {
        super(outPathName, extensionParameters);
    }

    @Override
    public void beginConst(ConstType constType) throws ZserioEmitException
    {
        final ConstEmitterTemplateData templateData = new ConstEmitterTemplateData(getTemplateDataContext(),
                constType);
        processHeaderTemplate(TEMPLATE_HEADER_NAME, templateData, constType);
    }

    private static final String TEMPLATE_HEADER_NAME = "ConstType.h.ftl";
}
