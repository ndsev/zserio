package zserio.extension.cpp;

import java.math.BigInteger;

import zserio.extension.common.ZserioExtensionException;
import zserio.extension.cpp.types.NativeIntegralType;

/**
 * The class to format zserio literals in C++ format.
 */
final class CppLiteralFormatter
{
    /**
     * Formats an integral value as uint8_t literal.
     *
     * @throws ZserioExtensionException Throws if integral value is out of range.
     */
    public static String formatUInt8Literal(int value) throws ZserioExtensionException
    {
        return uint8Type.formatLiteral(BigInteger.valueOf(value));
    }

    private final static NativeIntegralType uint8Type = new NativeIntegralType(8, false);
}
