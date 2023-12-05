package zserio.extension.java;

import zserio.ast.UnionType;
import zserio.extension.common.OutputFileManager;
import zserio.extension.common.ZserioExtensionException;

/**
 * Union emitter.
 */
final class UnionEmitter extends JavaDefaultEmitter
{
    public UnionEmitter(OutputFileManager outputFileManager, JavaExtensionParameters javaParameters)
    {
        super(outputFileManager, javaParameters);
    }

    @Override
    public void beginUnion(UnionType unionType) throws ZserioExtensionException
    {
        Object templateData = new UnionEmitterTemplateData(getTemplateDataContext(), unionType);
        processTemplate(TEMPLATE_NAME, templateData, unionType);
    }

    private static final String TEMPLATE_NAME = "Union.java.ftl";
}
