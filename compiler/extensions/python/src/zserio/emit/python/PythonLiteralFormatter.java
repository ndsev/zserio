package zserio.emit.python;

import java.math.BigInteger;

final class PythonLiteralFormatter
{
    /**
     * Formats boolean value to python format.
     *
     * @param value Boolean value to format.
     * @return The boolean value in Python format.
     */
    public static String formatBooleanLiteral(boolean value)
    {
        if (value)
            return "True";
        else
            return "False";
    }

    /**
     * Formats an integral value to python decimal literal.
     *
     * @param value Integral value to format.
     * @return Decimal literal in Python format.
     */
    public static String formatDecimalLiteral(BigInteger value)
    {
        return value.toString();
    }

    /**
     * Formats an integral value to python decimal literal.
     *
     * @param value Integral value to format.
     * @return Decimal literal in Python format.
     */
    public static String formatDecimalLiteral(int value)
    {
        return Integer.toString(value);
    }
}