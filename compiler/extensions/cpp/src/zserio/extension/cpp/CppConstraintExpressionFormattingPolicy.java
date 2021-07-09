package zserio.extension.cpp;

import zserio.ast.Field;

/**
 * A special formatting policy to be used in generated private read methods for constraint expressions.
 *
 * Constraint expressions can access just read field which has not been initialized yet.
 */
public class CppConstraintExpressionFormattingPolicy extends CppExpressionFormattingPolicy
{
    public CppConstraintExpressionFormattingPolicy(CppNativeMapper cppNativeMapper,
            IncludeCollector includeCollector, Field constraintField)
    {
        super(cppNativeMapper, includeCollector);

        this.constraintField = constraintField;
    }

    @Override
    protected void formatFieldGetter(StringBuilder result, Field field)
    {
        if (field == constraintField)
            result.append(CONTRAINT_FIELD_VARIABLE);
        else
            super.formatFieldGetter(result, field);
    }

    private static final String CONTRAINT_FIELD_VARIABLE = "readField";

    private final Field constraintField;
}
