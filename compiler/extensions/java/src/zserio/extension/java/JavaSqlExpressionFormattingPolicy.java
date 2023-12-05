package zserio.extension.java;

import zserio.ast.ZserioType;

/**
 * A special formatting policy to be used in generated SQL tables which need indirect references.
 */
public final class JavaSqlExpressionFormattingPolicy extends JavaExpressionFormattingPolicy
{
    public JavaSqlExpressionFormattingPolicy(JavaNativeMapper javaNativeMapper)
    {
        super(javaNativeMapper);
    }

    @Override
    protected String getAccessPrefixForCompoundType(ZserioType owner)
    {
        return "row";
    }
}
