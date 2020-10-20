package zserio.emit.java;

import zserio.ast.UnionType;
import zserio.emit.common.ZserioEmitException;
import zserio.tools.Parameters;

class UnionEmitter extends JavaDefaultEmitter
{
    public UnionEmitter(JavaExtensionParameters javaParameters, Parameters extensionParameters)
    {
        super(javaParameters, extensionParameters);
    }

    @Override
    public void beginUnion(UnionType unionType) throws ZserioEmitException
    {
        Object templateData = new UnionEmitterTemplateData(getTemplateDataContext(), unionType);
        processTemplate(TEMPLATE_NAME, templateData, unionType);
    }

    private static final String TEMPLATE_NAME = "Union.java.ftl";
}
