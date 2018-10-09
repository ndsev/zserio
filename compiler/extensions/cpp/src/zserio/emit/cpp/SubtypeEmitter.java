package zserio.emit.cpp;

import zserio.ast.Subtype;
import zserio.tools.Parameters;

public class SubtypeEmitter extends CppDefaultEmitter
{
    public SubtypeEmitter(String outPathName, Parameters extensionParameters)
    {
        super(outPathName, extensionParameters);
    }

    @Override
    public void beginSubtype(Subtype subtype) throws ZserioEmitCppException
    {
        Object templateData = new SubtypeEmitterTemplateData(getTemplateDataContext(), subtype);

        processHeaderTemplate(TEMPLATE_HEADER_NAME, templateData, subtype);
    }

    private static final String TEMPLATE_HEADER_NAME = "Subtype.h.ftl";
}
