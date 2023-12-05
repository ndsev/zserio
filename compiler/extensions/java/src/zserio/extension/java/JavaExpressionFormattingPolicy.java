package zserio.extension.java;

import zserio.ast.EnumType;
import zserio.ast.ZserioType;
import zserio.extension.common.ZserioExtensionException;
import zserio.extension.java.types.JavaNativeType;

/**
 * Formatting policy for Java expressions.
 *
 * This policy does work for everything except for case labels.
 */
public class JavaExpressionFormattingPolicy extends JavaDefaultExpressionFormattingPolicy
{
    public JavaExpressionFormattingPolicy(JavaNativeMapper javaNativeMapper)
    {
        super(javaNativeMapper);
    }

    @Override
    protected String getIdentifierForTypeEnum(EnumType resolvedType, JavaNativeMapper javaNativeMapper)
            throws ZserioExtensionException
    {
        final JavaNativeType javaType = javaNativeMapper.getJavaType(resolvedType);

        return javaType.getFullName();
    }

    @Override
    protected String getDotSeparatorForEnumItem()
    {
        return ".";
    }

    @Override
    protected String getAccessPrefixForCompoundType(ZserioType owner)
    {
        return "";
    }
}
