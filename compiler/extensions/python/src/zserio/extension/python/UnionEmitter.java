package zserio.extension.python;

import zserio.ast.UnionType;
import zserio.extension.common.ZserioExtensionException;
import zserio.tools.ExtensionParameters;

public class UnionEmitter extends PythonDefaultEmitter
{
    public UnionEmitter(String outputPath, ExtensionParameters extensionParameters)
    {
        super(outputPath, extensionParameters);
    }

    @Override
    public void beginUnion(UnionType unionType) throws ZserioExtensionException
    {
        final Object templateData = new UnionEmitterTemplateData(getTemplateDataContext(), unionType);
        processSourceTemplate(TEMPLATE_SOURCE_NAME, templateData, unionType);
    }

    private static final String TEMPLATE_SOURCE_NAME = "Union.py.ftl";
}
