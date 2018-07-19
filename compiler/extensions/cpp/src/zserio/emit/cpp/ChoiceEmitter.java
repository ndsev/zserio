package zserio.emit.cpp;

import java.util.ArrayList;
import java.util.List;

import antlr.collections.AST;
import zserio.ast.ChoiceType;
import zserio.tools.Parameters;

public class ChoiceEmitter extends CppDefaultEmitter
{
    public ChoiceEmitter(String outPathName, Parameters extensionParameters)
    {
        super(outPathName, extensionParameters);
        choiceTypeList = new ArrayList<ChoiceType>();
    }

    @Override
    public void beginChoice(AST token) throws ZserioEmitCppException
    {
        if (!(token instanceof ChoiceType))
            throw new ZserioEmitCppException("Unexpected token type in beginChoice!");
        choiceTypeList.add((ChoiceType)token);
    }

    @Override
    public void endRoot() throws ZserioEmitCppException
    {
        final TemplateDataContext templateDataContext = getTemplateDataContext();
        for (ChoiceType choiceType : choiceTypeList)
        {
            final Object templateData = new ChoiceEmitterTemplateData(templateDataContext, choiceType);

            processHeaderTemplate(TEMPLATE_HEADER_NAME, templateData, choiceType);
            processSourceTemplate(TEMPLATE_SOURCE_NAME, templateData, choiceType);
        }
    }

    private final List<ChoiceType> choiceTypeList;

    private static final String TEMPLATE_SOURCE_NAME = "Choice.cpp.ftl";
    private static final String TEMPLATE_HEADER_NAME = "Choice.h.ftl";
}
