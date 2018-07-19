package zserio.emit.cpp;

import antlr.collections.AST;
import zserio.ast.EnumType;
import zserio.tools.Parameters;

public class EnumerationEmitter extends CppDefaultEmitter
{
    public EnumerationEmitter(String outPathName, Parameters extensionParameters)
    {
        super(outPathName, extensionParameters);
    }

    @Override
    public void beginEnumeration(AST token) throws ZserioEmitCppException
    {
        if (!(token instanceof EnumType))
            throw new ZserioEmitCppException("Unexpected token type in beginEnumeration!");

        final EnumType enumType = (EnumType)token;
        final Object templateData = new EnumerationEmitterTemplateData(getTemplateDataContext(), enumType);

        processHeaderTemplate(TEMPLATE_HEADER_NAME, templateData, enumType);
        processSourceTemplate(TEMPLATE_SOURCE_NAME, templateData, enumType);
    }

    private static final String TEMPLATE_SOURCE_NAME = "Enumeration.cpp.ftl";
    private static final String TEMPLATE_HEADER_NAME = "Enumeration.h.ftl";
}
