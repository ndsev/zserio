package zserio.emit.cpp;

import java.math.BigInteger;

import zserio.emit.cpp.types.NativeBooleanType;
import zserio.emit.cpp.types.NativeStdIntType;

/**
 * The class to format zserio literals in C++ format.
 */
final class CppLiteralFormatter
{
    /**
     * Formats boolean value in C++ format.
     *
     * @param value Boolean value to format.
     *
     * @return The boolean value in C++ format.
     */
    public static String formatBooleanLiteral(boolean value)
    {
        return booleanType.formatLiteral(value);
    }

    /**
     * Formats an integral value as uint8_t literal.
     */
    public static String formatUInt8Literal(int value)
    {
        return uint8Type.formatLiteral(BigInteger.valueOf(value));
    }

    private final static NativeBooleanType booleanType = new NativeBooleanType();
    private final static NativeStdIntType uint8Type = new NativeStdIntType(8, false);
}
