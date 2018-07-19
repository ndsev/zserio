package zserio.emit.cpp;

import java.util.ArrayList;
import java.util.List;

import antlr.collections.AST;
import zserio.ast.UnionType;
import zserio.tools.Parameters;

public class UnionEmitter extends CppDefaultEmitter
{
    public UnionEmitter(String outPathName, Parameters extensionParameters)
    {
        super(outPathName, extensionParameters);
        unionTypeList = new ArrayList<UnionType>();
    }

    @Override
    public void beginUnion(AST token) throws ZserioEmitCppException
    {
        if (!(token instanceof UnionType))
            throw new ZserioEmitCppException("Unexpected token type in beginUnion!");
        unionTypeList.add((UnionType)token);
    }

    @Override
    public void endRoot() throws ZserioEmitCppException
    {
        final TemplateDataContext templateDataContext = getTemplateDataContext();
        for (UnionType unionType : unionTypeList)
        {
            final Object templateData = new UnionEmitterTemplateData(templateDataContext, unionType);

            processHeaderTemplate(TEMPLATE_HEADER_NAME, templateData, unionType);
            processSourceTemplate(TEMPLATE_SOURCE_NAME, templateData, unionType);
        }
    }

    private final List<UnionType> unionTypeList;

    private static final String TEMPLATE_HEADER_NAME = "Union.h.ftl";
    private static final String TEMPLATE_SOURCE_NAME = "Union.cpp.ftl";
}
