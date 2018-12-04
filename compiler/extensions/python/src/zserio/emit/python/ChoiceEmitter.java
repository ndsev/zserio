package zserio.emit.python;

import zserio.ast.ChoiceType;
import zserio.emit.common.ZserioEmitException;
import zserio.tools.Parameters;

public class ChoiceEmitter extends PythonDefaultEmitter
{
    public ChoiceEmitter(String outputPath, Parameters extensionParameters)
    {
        super(outputPath, extensionParameters);
    }

    @Override
    public void beginChoice(ChoiceType choiceType) throws ZserioEmitException
    {
        final Object templateData = new ChoiceEmitterTemplateData(getTemplateDataContext(), choiceType);
        processSourceTemplate(TEMPLATE_SOURCE_NAME, templateData, choiceType);
    }

    private static final String TEMPLATE_SOURCE_NAME = "Choice.py.ftl";
}