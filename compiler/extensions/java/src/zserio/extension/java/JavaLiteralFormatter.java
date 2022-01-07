package zserio.extension.java;

import java.math.BigInteger;

import zserio.extension.common.ZserioExtensionException;
import zserio.extension.java.types.NativeArrayTraits;
import zserio.extension.java.types.NativeBooleanType;
import zserio.extension.java.types.NativeIntType;

/**
 * The class to format zserio literals in Java format.
 */
final class JavaLiteralFormatter
{
    public static String formatBooleanLiteral(boolean value)
    {
        return booleanType.formatLiteral(value);
    }

    public static String formatIntLiteral(int value) throws ZserioExtensionException
    {
        return intType.formatLiteral(BigInteger.valueOf(value));
    }

    private final static NativeBooleanType booleanType = new NativeBooleanType(false);
    private final static NativeIntType intType = new NativeIntType(
            false, new NativeArrayTraits("BitFieldIntArray"));
}
