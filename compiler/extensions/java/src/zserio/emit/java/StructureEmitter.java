package zserio.emit.java;

import antlr.collections.AST;
import zserio.ast.StructureType;
import zserio.tools.Parameters;

class StructureEmitter extends JavaDefaultEmitter
{
    public StructureEmitter(Parameters extensionParameters, JavaExtensionParameters javaParameters)
    {
        super(extensionParameters, javaParameters);
    }

    /** {@inheritDoc} */
    @Override
    public void beginStructure(AST token) throws ZserioEmitJavaException
    {
        if (!(token instanceof StructureType))
            throw new ZserioEmitJavaException("Unexpected token type in beginStructure!");

        final StructureType structureType = (StructureType) token;
        Object templateData = new StructureEmitterTemplateData(getTemplateDataContext(), structureType);
        processTemplate(TEMPLATE_NAME, templateData, structureType);
    }

    private static final String TEMPLATE_NAME = "Structure.java.ftl";
}
