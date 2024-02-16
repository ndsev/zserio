package zserio.extension.cpp;

import zserio.ast.ArrayInstantiation;
import zserio.ast.Field;

/**
 * A special formatting policy to be used in generated private read methods for constraint expressions.
 *
 * Constraint expressions can access just read field which has not been initialized yet.
 */
public final class CppConstraintExpressionFormattingPolicy extends CppExpressionFormattingPolicy
{
    public CppConstraintExpressionFormattingPolicy(
            TemplateDataContext context, IncludeCollector includeCollector, Field constraintField)
    {
        super(context, includeCollector);

        this.constraintField = constraintField;
    }

    @Override
    protected void formatFieldGetter(StringBuilder result, boolean isMostLeftId, Field field)
    {
        if (isMostLeftId && field == constraintField)
        {
            result.append(CONTRAINT_FIELD_VARIABLE);
            if (field.getTypeInstantiation() instanceof ArrayInstantiation)
                result.append(RAW_ARRAY_ACCESSOR);
        }
        else
        {
            super.formatFieldGetter(result, isMostLeftId, field);
        }
    }

    private static final String CONTRAINT_FIELD_VARIABLE = "readField";
    private static final String RAW_ARRAY_ACCESSOR = ".getRawArray()";

    private final Field constraintField;
}
