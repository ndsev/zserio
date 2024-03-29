package zserio.extension.python;

import zserio.ast.UnionType;
import zserio.extension.common.OutputFileManager;
import zserio.extension.common.PackedTypesCollector;
import zserio.extension.common.ZserioExtensionException;

/**
 * Union emitter.
 */
final class UnionEmitter extends PythonDefaultEmitter
{
    public UnionEmitter(OutputFileManager outputFileManager, PythonExtensionParameters pythonParameters,
            PackedTypesCollector packedTypesCollector)
    {
        super(outputFileManager, pythonParameters,
                new ChoiceTemplateDataContext(pythonParameters, packedTypesCollector));
    }

    @Override
    public void beginUnion(UnionType unionType) throws ZserioExtensionException
    {
        final UnionEmitterTemplateData templateData =
                new UnionEmitterTemplateData(getTemplateDataContext(), unionType);
        processSourceTemplate(TEMPLATE_SOURCE_NAME, templateData, unionType);
    }

    static final String TEMPLATE_SOURCE_NAME = "Union.py.ftl";
}
