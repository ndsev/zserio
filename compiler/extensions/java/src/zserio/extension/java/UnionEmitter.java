package zserio.extension.java;

import zserio.ast.UnionType;
import zserio.extension.common.ZserioExtensionException;
import zserio.tools.ExtensionParameters;

class UnionEmitter extends JavaDefaultEmitter
{
    public UnionEmitter(JavaExtensionParameters javaParameters, ExtensionParameters extensionParameters)
    {
        super(javaParameters, extensionParameters);
    }

    @Override
    public void beginUnion(UnionType unionType) throws ZserioExtensionException
    {
        Object templateData = new UnionEmitterTemplateData(getTemplateDataContext(), unionType);
        processTemplate(TEMPLATE_NAME, templateData, unionType);
    }

    private static final String TEMPLATE_NAME = "Union.java.ftl";
}
