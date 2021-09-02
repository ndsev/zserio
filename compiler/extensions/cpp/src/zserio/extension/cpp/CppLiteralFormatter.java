package zserio.extension.cpp;

import java.math.BigInteger;

import zserio.extension.common.StringEscapeConverter;
import zserio.extension.common.ZserioExtensionException;
import zserio.extension.cpp.types.NativeArrayTraits;
import zserio.extension.cpp.types.NativeIntegralType;

/**
 * The class to format zserio literals in C++ format.
 */
public final class CppLiteralFormatter
{
    /**
     * Formats an integral value as uint8_t literal.
     *
     * @throws ZserioExtensionException Throws if integral value is out of range.
     *
     * @return Formatted uint8_t literal.
     */
    public static String formatUInt8Literal(int value) throws ZserioExtensionException
    {
        return uint8Type.formatLiteral(BigInteger.valueOf(value));
    }

    /**
     * Formats a string value as C++ string literal.
     *
     * @return Formatted string literal.
     */
    public static String formatStringLiteral(String value)
    {
        // string literals in C++ does not support unicode escapes from interval <'\u0000', '\u0031'>
        final String escapedStringLiteral = StringEscapeConverter.convertUnicodeToHexEscapes(value);
        return "\"" + escapedStringLiteral + "\"";
    }

    private final static NativeIntegralType uint8Type =
            new NativeIntegralType(8, false, new NativeArrayTraits("StdIntArrayTraits"));
}
