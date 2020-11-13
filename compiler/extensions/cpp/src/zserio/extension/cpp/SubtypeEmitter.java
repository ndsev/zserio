package zserio.extension.cpp;

import zserio.ast.Subtype;
import zserio.extension.common.ZserioExtensionException;
import zserio.tools.ExtensionParameters;

public class SubtypeEmitter extends CppDefaultEmitter
{
    public SubtypeEmitter(String outPathName, ExtensionParameters extensionParameters)
    {
        super(outPathName, extensionParameters);
    }

    @Override
    public void beginSubtype(Subtype subtype) throws ZserioExtensionException
    {
        Object templateData = new SubtypeEmitterTemplateData(getTemplateDataContext(), subtype);

        processHeaderTemplate(TEMPLATE_HEADER_NAME, templateData, subtype);
    }

    private static final String TEMPLATE_HEADER_NAME = "Subtype.h.ftl";
}
