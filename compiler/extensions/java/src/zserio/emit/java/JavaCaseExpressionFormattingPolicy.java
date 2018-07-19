package zserio.emit.java;

import zserio.ast.EnumItem;
import zserio.ast.EnumType;

/**
 * A special formatting policy to be used in Java case expressions.
 *
 * Java language requires that for switches over an enum type the individual cases
 * use only the enum item name, not the full name (e.g. "case RED:" instead of "case Color.RED:").
 * This is different from other expressions where the full name ("Color.RED") must be used.
 */
public class JavaCaseExpressionFormattingPolicy extends JavaDefaultExpressionFormattingPolicy
{
    public JavaCaseExpressionFormattingPolicy(JavaNativeTypeMapper javaNativeTypeMapper)
    {
        super(javaNativeTypeMapper);
    }

    @Override
    protected String getIdentifierForTypeEnum(EnumType resolvedType, JavaNativeTypeMapper javaNativeTypeMapper)
    {
        return "";
    }

    @Override
    protected String getIdentifierForEnumItem(EnumItem enumItem)
    {
        return enumItem.getName();
    }

    @Override
    protected String getDotSeparatorForEnumItem()
    {
        return "";
    }

    @Override
    protected String getAccessPrefixForCompoundType()
    {
        return "";
    }
}
