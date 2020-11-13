package zserio.extension.java;

import java.math.BigInteger;

import zserio.extension.common.ZserioExtensionException;
import zserio.extension.java.types.NativeBooleanType;
import zserio.extension.java.types.NativeIntType;

/**
 * The class to format zserio literals in Java format.
 */
final class JavaLiteralFormatter
{
    /**
     * Formats boolean value in Java format.
     *
     * @param value Boolean value to format.
     *
     * @return The boolean value in Java format.
     */
    public static String formatBooleanLiteral(boolean value)
    {
        return booleanType.formatLiteral(value);
    }

    /**
     * Formats decimal integer value in Java format.
     *
     * @param value Integer value to format.
     *
     * @return The decimal integer value in Java format.
     *
     * @throws ZserioExtensionException Throws in case of any range error.
     */
    public static String formatDecimalLiteral(int value) throws ZserioExtensionException
    {
        return intType.formatLiteral(BigInteger.valueOf(value));
    }

    private final static NativeBooleanType booleanType = new NativeBooleanType(false);
    private final static NativeIntType intType = new NativeIntType(false);
}
