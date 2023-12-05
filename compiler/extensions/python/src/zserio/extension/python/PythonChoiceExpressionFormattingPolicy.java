package zserio.extension.python;

import zserio.ast.Field;
import zserio.ast.Parameter;

/**
 * A special formatting policy to be used in choices and unions which have single member for all fields.
 */
final class PythonChoiceExpressionFormattingPolicy extends PythonExpressionFormattingPolicy
{
    public PythonChoiceExpressionFormattingPolicy(TemplateDataContext context,
            ImportCollector importCollector)
    {
        super(context, importCollector);
    }

    public PythonChoiceExpressionFormattingPolicy(TemplateDataContext context,
            ImportCollector importCollector, String accessPrefix)
    {
        super(context, importCollector, accessPrefix);
    }

    @Override
    protected void formatFieldAccessor(StringBuilder result, boolean isFirstInDot, Field field,
            boolean isSetter)
    {
        if (isFirstInDot && !isSetter)
        {
            result.append(getAccessPrefix());
            result.append(CHOICE_MEMBER_NAME);
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
            result.append(getAccessPrefix());
            result.append(AccessorNameFormatter.getMemberName(param));
        }
        else
        {
            super.formatParameterAccessor(result, false, param);
        }
    }

    private static final String CHOICE_MEMBER_NAME = "_choice";
}
