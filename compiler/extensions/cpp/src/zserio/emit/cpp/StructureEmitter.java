package zserio.emit.cpp;

import java.util.ArrayList;
import java.util.List;

import antlr.collections.AST;
import zserio.ast.StructureType;
import zserio.tools.Parameters;

public class StructureEmitter extends CppDefaultEmitter
{
    public StructureEmitter(String outPathName, Parameters extensionParameters)
    {
        super(outPathName, extensionParameters);
        structureTypeList = new ArrayList<StructureType>();
    }

    @Override
    public void beginStructure(AST token) throws ZserioEmitCppException
    {
        if (!(token instanceof StructureType))
            throw new ZserioEmitCppException("Unexpected token type in beginStructure!");
        structureTypeList.add((StructureType)token);
    }

    @Override
    public void endRoot() throws ZserioEmitCppException
    {
        final TemplateDataContext templateDataContext = getTemplateDataContext();
        for (StructureType structureType : structureTypeList)
        {
            final Object templateData = new StructureEmitterTemplateData(templateDataContext, structureType);

            processHeaderTemplate(TEMPLATE_HEADER_NAME, templateData, structureType);
            processSourceTemplate(TEMPLATE_SOURCE_NAME, templateData, structureType);
        }
    }

    private final List<StructureType> structureTypeList;

    private static final String TEMPLATE_SOURCE_NAME = "Structure.cpp.ftl";
    private static final String TEMPLATE_HEADER_NAME = "Structure.h.ftl";
}
