package zserio.extension.python;

import zserio.ast.UnionType;
import zserio.extension.common.OutputFileManager;
import zserio.extension.common.ZserioExtensionException;

public class UnionEmitter extends PythonDefaultEmitter
{
    public UnionEmitter(OutputFileManager outputFileManager, PythonExtensionParameters pythonParameters)
    {
        super(outputFileManager, pythonParameters);
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
