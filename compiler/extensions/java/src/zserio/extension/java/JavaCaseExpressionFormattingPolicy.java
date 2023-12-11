package zserio.extension.java;

import zserio.ast.EnumType;
import zserio.ast.ZserioType;

/**
 * A special formatting policy to be used in Java case expressions.
 *
 * Java language requires that for switches over an enum type the individual cases
 * use only the enum item name, not the full name (e.g. "case RED:" instead of "case Color.RED:").
 * This is different from other expressions where the full name ("Color.RED") must be used.
 */
public final class JavaCaseExpressionFormattingPolicy extends JavaDefaultExpressionFormattingPolicy
{
    public JavaCaseExpressionFormattingPolicy(JavaNativeMapper javaNativeMapper)
    {
        super(javaNativeMapper);
    }

    @Override
    protected String getIdentifierForTypeEnum(EnumType resolvedType, JavaNativeMapper javaNativeMapper)
    {
        return "";
    }

    @Override
    protected String getDotSeparatorForEnumItem()
    {
        return "";
    }

    @Override
    protected String getAccessPrefixForCompoundType(ZserioType owner)
    {
        return "";
    }
}
