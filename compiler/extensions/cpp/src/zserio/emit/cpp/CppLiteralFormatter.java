package zserio.emit.cpp;

import java.math.BigInteger;

import zserio.emit.common.ZserioEmitException;
import zserio.emit.cpp.types.NativeIntegralType;

/**
 * The class to format zserio literals in C++ format.
 */
final class CppLiteralFormatter
{
    /**
     * Formats an integral value as uint8_t literal.
     *
     * @throws ZserioEmitException Throws if integral value is out of range.
     */
    public static String formatUInt8Literal(int value) throws ZserioEmitException
    {
        return uint8Type.formatLiteral(BigInteger.valueOf(value));
    }

    private final static NativeIntegralType uint8Type = new NativeIntegralType(8, false);
}
