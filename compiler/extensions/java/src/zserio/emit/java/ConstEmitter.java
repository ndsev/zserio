package zserio.emit.java;

import java.util.ArrayList;
import java.util.List;

import zserio.ast.ConstType;
import zserio.ast.Root;
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
        constTypes.add(constType);
    }

    @Override
    public void endRoot(Root root) throws ZserioEmitException
    {
        if (!constTypes.isEmpty())
        {
            templateData = new ConstEmitterTemplateData(getTemplateDataContext(), constTypes);
            processTemplateToRootDir(TEMPLATE_NAME, templateData, CONST_CLASS_FILE_NAME);
        }
    }

    private static final String TEMPLATE_NAME = "ConstType.java.ftl";
    private static final String CONST_CLASS_FILE_NAME = "__ConstType";

    private final List<ConstType> constTypes = new ArrayList<ConstType>();
    private ConstEmitterTemplateData templateData;
}
