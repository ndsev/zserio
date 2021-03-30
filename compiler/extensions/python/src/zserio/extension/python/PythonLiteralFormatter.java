package zserio.extension.python;

import java.math.BigInteger;

/**
 * The class provides formatting of various Python literals.
 */
final class PythonLiteralFormatter
{
    public static String formatBooleanLiteral(boolean value)
    {
        if (value)
            return "True";
        else
            return "False";
    }

    public static String formatDecimalLiteral(BigInteger value)
    {
        return value.toString();
    }

    public static String formatDecimalLiteral(int value)
    {
        return Integer.toString(value);
    }
}