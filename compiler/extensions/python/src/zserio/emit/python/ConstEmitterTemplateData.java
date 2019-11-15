package zserio.emit.python;

import zserio.ast.Constant;
import zserio.emit.common.ExpressionFormatter;
import zserio.emit.common.ZserioEmitException;

public class ConstEmitterTemplateData extends PythonTemplateData
{
    public ConstEmitterTemplateData(TemplateDataContext context, Constant constant)
            throws ZserioEmitException
    {
        super(context);

        name = constant.getName();

        final ExpressionFormatter pythonExpressionFormatter = context.getPythonExpressionFormatter(this);
        value = pythonExpressionFormatter.formatGetter(constant.getValueExpression());
    }

    public String getName()
    {
        return name;
    }

    public String getValue()
    {
        return value;
    }

    private final String name;
    private final String value;
}
