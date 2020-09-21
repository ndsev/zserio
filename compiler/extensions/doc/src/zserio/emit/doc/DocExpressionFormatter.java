package zserio.emit.doc;

import zserio.ast.Expression;
import zserio.emit.common.ExpressionFormatter;
import zserio.emit.common.ZserioEmitException;

public class DocExpressionFormatter
{
    public DocExpressionFormatter(ExpressionFormatter expressionFormatter)
    {
        this.expressionFormatter = expressionFormatter;
    }

    public String formatExpression(Expression expression) throws ZserioEmitException
    {
        if (expression == null)
            return "";
        return expressionFormatter.formatGetter(expression);
    }

    private final ExpressionFormatter expressionFormatter;
}
