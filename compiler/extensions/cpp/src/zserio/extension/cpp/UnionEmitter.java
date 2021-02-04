package zserio.extension.cpp;

import zserio.ast.UnionType;
import zserio.extension.common.ZserioExtensionException;

public class UnionEmitter extends CppDefaultEmitter
{
    public UnionEmitter(CppExtensionParameters cppParameters)
    {
        super(cppParameters);
    }

    @Override
    public void beginUnion(UnionType unionType) throws ZserioExtensionException
    {
        final Object templateData = new UnionEmitterTemplateData(getTemplateDataContext(), unionType);

        processHeaderTemplate(TEMPLATE_HEADER_NAME, templateData, unionType);
        processSourceTemplate(TEMPLATE_SOURCE_NAME, templateData, unionType);
    }

    private static final String TEMPLATE_HEADER_NAME = "Union.h.ftl";
    private static final String TEMPLATE_SOURCE_NAME = "Union.cpp.ftl";
}
