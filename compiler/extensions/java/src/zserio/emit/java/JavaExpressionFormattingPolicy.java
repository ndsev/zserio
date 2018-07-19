package zserio.emit.java;

import zserio.ast.EnumItem;
import zserio.ast.EnumType;
import zserio.emit.java.types.JavaNativeType;

/**
 * Formatting policy for Java expressions.
 *
 * This policy does work for everything except for case labels.
 */
public class JavaExpressionFormattingPolicy extends JavaDefaultExpressionFormattingPolicy
{
    public JavaExpressionFormattingPolicy(JavaNativeTypeMapper javaNativeTypeMapper)
    {
        super(javaNativeTypeMapper);
    }

    @Override
    protected String getIdentifierForTypeEnum(EnumType resolvedType, JavaNativeTypeMapper javaNativeTypeMapper)
    {
        final JavaNativeType javaType = javaNativeTypeMapper.getJavaType(resolvedType);
        return javaType.getFullName();
    }

    @Override
    protected String getIdentifierForEnumItem(EnumItem enumItem)
    {
        return enumItem.getName();
    }

    @Override
    protected String getDotSeparatorForEnumItem()
    {
        return ".";
    }

    @Override
    protected String getAccessPrefixForCompoundType()
    {
        return "";
    }
}
