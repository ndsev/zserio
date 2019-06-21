package zserio.emit.cpp98;

import java.math.BigInteger;

import zserio.emit.common.ZserioEmitException;
import zserio.emit.cpp98.types.NativeBooleanType;
import zserio.emit.cpp98.types.NativeStdIntType;

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
     *
     * @throws ZserioEmitException Throws if integral value is out of range.
     */
    public static String formatUInt8Literal(int value) throws ZserioEmitException
    {
        return uint8Type.formatLiteral(BigInteger.valueOf(value));
    }

    private final static NativeBooleanType booleanType = new NativeBooleanType();
    private final static NativeStdIntType uint8Type = new NativeStdIntType(8, false);
}
