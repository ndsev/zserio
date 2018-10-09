package zserio.emit.cpp;

import java.util.ArrayList;
import java.util.List;

import zserio.ast.ConstType;
import zserio.ast.Root;
import zserio.tools.Parameters;

public class ConstEmitter extends CppDefaultEmitter
{
    public ConstEmitter(String outPathName, Parameters extensionParameters)
    {
        super(outPathName, extensionParameters);
    }

    @Override
    public void beginConst(ConstType constType) throws ZserioEmitCppException
    {
        constTypes.add(constType);
    }

    @Override
    public void endRoot(Root root) throws ZserioEmitCppException
    {
        if (!constTypes.isEmpty())
        {
            final ConstEmitterTemplateData templateData =
                    new ConstEmitterTemplateData(getTemplateDataContext(), constTypes);
            processHeaderTemplateToRootDir(TEMPLATE_HEADER_NAME, templateData, OUTPUT_FILE_NAME_ROOT);
        }
    }

    private static final String TEMPLATE_HEADER_NAME = "ConstType.h.ftl";
    private static final String OUTPUT_FILE_NAME_ROOT = "ConstType";

    private final List<ConstType> constTypes = new ArrayList<ConstType>();
}
