package zserio.emit.python;

import zserio.ast.Field;

/**
 * A special formatting policy to be used in generated SQL tables which need indirect references.
 */
public class PythonSqlIndirectExpressionFormattingPolicy extends PythonExpressionFormattingPolicy
{
    public PythonSqlIndirectExpressionFormattingPolicy(PythonNativeMapper pythonNativeMapper,
            ImportCollector importCollector)
    {
        super(pythonNativeMapper, importCollector);
    }

    @Override
    protected void formatFieldAccessor(StringBuilder result, boolean isFirstInDot, Field field,
            boolean isSetter)
    {
        if (isFirstInDot)
            result.append(AccessorNameFormatter.getSqlColumnName(field));
        else
            super.formatFieldAccessor(result, false, field, isSetter);
    }
}
