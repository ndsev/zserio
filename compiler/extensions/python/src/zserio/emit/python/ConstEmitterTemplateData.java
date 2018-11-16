package zserio.emit.python;

import zserio.ast.ConstType;
import zserio.emit.common.ExpressionFormatter;
import zserio.emit.common.ZserioEmitException;

public class ConstEmitterTemplateData extends PythonTemplateData
{
    public ConstEmitterTemplateData(TemplateDataContext context, ConstType constType)
            throws ZserioEmitException
    {
        name = constType.getName();

        final ExpressionFormatter pythonExpressionFormatter = context.getExpressionFormatter(this);
        value = pythonExpressionFormatter.formatGetter(constType.getValueExpression());
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
