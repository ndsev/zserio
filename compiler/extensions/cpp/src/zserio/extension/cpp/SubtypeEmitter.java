package zserio.extension.cpp;

import zserio.ast.Subtype;
import zserio.extension.common.ZserioExtensionException;

public class SubtypeEmitter extends CppDefaultEmitter
{
    public SubtypeEmitter(CppExtensionParameters cppParameters)
    {
        super(cppParameters);
    }

    @Override
    public void beginSubtype(Subtype subtype) throws ZserioExtensionException
    {
        Object templateData = new SubtypeEmitterTemplateData(getTemplateDataContext(), subtype);

        processHeaderTemplate(TEMPLATE_HEADER_NAME, templateData, subtype);
    }

    private static final String TEMPLATE_HEADER_NAME = "Subtype.h.ftl";
}
