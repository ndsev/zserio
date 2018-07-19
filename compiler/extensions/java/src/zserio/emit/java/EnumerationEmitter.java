package zserio.emit.java;

import antlr.collections.AST;
import zserio.ast.EnumType;
import zserio.tools.Parameters;

class EnumerationEmitter extends JavaDefaultEmitter
{
    public EnumerationEmitter(Parameters extensionParameters, JavaExtensionParameters javaParameters)
    {
        super(extensionParameters, javaParameters);
    }

    /** {@inheritDoc} */
    @Override
    public void beginEnumeration(AST token) throws ZserioEmitJavaException
    {
        if (!(token instanceof EnumType))
            throw new ZserioEmitJavaException("Unexpected token type in beginEnumeration!");

        final EnumType enumType = (EnumType)token;
        Object templateData = new EnumerationEmitterTemplateData(getTemplateDataContext(), enumType);
        processTemplate(TEMPLATE_NAME, templateData, enumType);
    }

    private static final String TEMPLATE_NAME = "Enumeration.java.ftl";
}
