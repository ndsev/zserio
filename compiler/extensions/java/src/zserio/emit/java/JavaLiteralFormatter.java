package zserio.emit.java;

import java.math.BigInteger;

import zserio.emit.java.types.NativeBooleanType;
import zserio.emit.java.types.NativeIntType;

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
     * Formats integer value in Java format.
     *
     * @param value Integer value to format.
     *
     * @return The integer value in Java format.
     */
    public static String formatIntegerLiteral(int value)
    {
        return intType.formatLiteral(BigInteger.valueOf(value));
    }

    private final static NativeBooleanType booleanType = new NativeBooleanType(false);
    private final static NativeIntType intType = new NativeIntType(false);
}
