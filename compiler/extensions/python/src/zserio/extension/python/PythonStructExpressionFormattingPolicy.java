package zserio.extension.python;

import zserio.ast.Field;
import zserio.ast.Parameter;

/**
 * A special formatting policy to be used in structures to access self members directly.
 */
class PythonStructExpressionFormattingPolicy extends PythonExpressionFormattingPolicy
{
    public PythonStructExpressionFormattingPolicy(TemplateDataContext context,
            ImportCollector importCollector)
    {
        super(context, importCollector);
    }

    @Override
    protected void formatFieldAccessor(StringBuilder result, boolean isFirstInDot, Field field,
            boolean isSetter)
    {
        if (isFirstInDot && !isSetter)
        {
            result.append(PYTHON_SELF_PREFIX);
            result.append(AccessorNameFormatter.getMemberName(field));
        }
        else
        {
            super.formatFieldAccessor(result, isFirstInDot, field, isSetter);
        }
    }

    @Override
    protected void formatParameterAccessor(StringBuilder result, boolean isFirstInDot, Parameter param)
    {
        if (isFirstInDot)
        {
            result.append(PYTHON_SELF_PREFIX);
            result.append(AccessorNameFormatter.getMemberName(param));
        }
        else
        {
            super.formatParameterAccessor(result, false, param);
        }
    }
}
