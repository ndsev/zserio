package zserio.extension.cpp;

import zserio.ast.ChoiceType;
import zserio.extension.common.ZserioExtensionException;

public class ChoiceEmitter extends CppDefaultEmitter
{
    public ChoiceEmitter(CppExtensionParameters cppParameters)
    {
        super(cppParameters);
    }

    @Override
    public void beginChoice(ChoiceType choiceType) throws ZserioExtensionException
    {
        final Object templateData = new ChoiceEmitterTemplateData(getTemplateDataContext(), choiceType);
        processHeaderTemplate(TEMPLATE_HEADER_NAME, templateData, choiceType);
        processSourceTemplate(TEMPLATE_SOURCE_NAME, templateData, choiceType);
    }

    private static final String TEMPLATE_SOURCE_NAME = "Choice.cpp.ftl";
    private static final String TEMPLATE_HEADER_NAME = "Choice.h.ftl";
}
