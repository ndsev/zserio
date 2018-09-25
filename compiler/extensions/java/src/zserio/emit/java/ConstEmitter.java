package zserio.emit.java;

import java.util.ArrayList;
import java.util.List;

import antlr.collections.AST;
import zserio.ast.ConstType;
import zserio.tools.Parameters;

class ConstEmitter extends JavaDefaultEmitter
{
    public ConstEmitter(Parameters extensionParameters, JavaExtensionParameters javaParameters)
    {
        super(extensionParameters, javaParameters);
    }

    /** {@inheritDoc} */
    @Override
    public void beginConst(AST token) throws ZserioEmitJavaException
    {
        if (!(token instanceof ConstType))
            throw new ZserioEmitJavaException("Unexpected token type in beginConst!");
        constTypes.add((ConstType)token);
    }

    /** {@inheritDoc} */
    @Override
    public void endRoot() throws ZserioEmitJavaException
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
