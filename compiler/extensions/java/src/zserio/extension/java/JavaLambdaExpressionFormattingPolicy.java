package zserio.extension.java;

import zserio.ast.ZserioType;

/**
 * A special formatting policy to be used in generated lambda functions for type info which need indirect
 * references.
 */
public class JavaLambdaExpressionFormattingPolicy extends JavaExpressionFormattingPolicy
{
    public JavaLambdaExpressionFormattingPolicy(JavaNativeMapper javaNativeMapper)
    {
        super(javaNativeMapper);
    }

    @Override
    protected String getAccessPrefixForCompoundType(ZserioType owner)
    {
        if (owner == null)
            return "obj";

        return "((" + owner.getName() + ")" + "obj)";
    }
}
