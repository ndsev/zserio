package zserio.emit.cpp;

import zserio.ast.BitmaskType;
import zserio.emit.common.ZserioEmitException;
import zserio.tools.Parameters;

public class BitmaskEmitter extends CppDefaultEmitter
{
    public BitmaskEmitter(String outPathName, Parameters extensionParameters)
    {
        super(outPathName, extensionParameters);
    }

    @Override
    public void beginBitmask(BitmaskType bitmaskType) throws ZserioEmitException
    {
        final Object templateData = new BitmaskEmitterTemplateData(getTemplateDataContext(), bitmaskType);

        processHeaderTemplate(TEMPLATE_HEADER_NAME, templateData, bitmaskType);
        processSourceTemplate(TEMPLATE_SOURCE_NAME, templateData, bitmaskType);
    }

    private static final String TEMPLATE_SOURCE_NAME = "Bitmask.cpp.ftl";
    private static final String TEMPLATE_HEADER_NAME = "Bitmask.h.ftl";
}
