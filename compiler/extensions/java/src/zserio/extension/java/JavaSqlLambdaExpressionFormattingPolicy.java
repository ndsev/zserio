package zserio.extension.java;

import zserio.ast.ZserioType;

/**
 * A special formatting policy to be used in generated lambda function in SQL tables for type info  which need
 * indirect references.
 */
public final class JavaSqlLambdaExpressionFormattingPolicy extends JavaExpressionFormattingPolicy
{
    public JavaSqlLambdaExpressionFormattingPolicy(JavaNativeMapper javaNativeMapper)
    {
        super(javaNativeMapper);
    }

    @Override
    protected String getAccessPrefixForCompoundType(ZserioType owner)
    {
        return "((" + owner.getName() + "Row)"
                + "row)";
    }
}
