package zserio.emit.cpp98;

import zserio.ast.ChoiceType;
import zserio.emit.common.ZserioEmitException;
import zserio.tools.Parameters;

public class ChoiceEmitter extends CppDefaultEmitter
{
    public ChoiceEmitter(String outPathName, Parameters extensionParameters)
    {
        super(outPathName, extensionParameters);
    }

    @Override
    public void beginChoice(ChoiceType choiceType) throws ZserioEmitException
    {
        final Object templateData = new ChoiceEmitterTemplateData(getTemplateDataContext(), choiceType);
        processHeaderTemplate(TEMPLATE_HEADER_NAME, templateData, choiceType);
        processSourceTemplate(TEMPLATE_SOURCE_NAME, templateData, choiceType);
    }

    private static final String TEMPLATE_SOURCE_NAME = "Choice.cpp.ftl";
    private static final String TEMPLATE_HEADER_NAME = "Choice.h.ftl";
}
