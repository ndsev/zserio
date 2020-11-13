package zserio.extension.python;

import zserio.ast.Constant;
import zserio.extension.common.ExpressionFormatter;
import zserio.extension.common.ZserioExtensionException;

public class ConstEmitterTemplateData extends PythonTemplateData
{
    public ConstEmitterTemplateData(TemplateDataContext context, Constant constant)
            throws ZserioExtensionException
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
