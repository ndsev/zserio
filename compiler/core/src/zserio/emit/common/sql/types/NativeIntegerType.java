package zserio.emit.common.sql.types;

import java.math.BigInteger;

/**
 * SQLite native type for Integers.
 */
public class NativeIntegerType implements SqlNativeType
{
    @Override
    public String getFullName()
    {
        return NAME;
    }

    @Override
    public String getName()
    {
        return NAME;
    }

    @Override
    public String getTraditionalName()
    {
        return NAME;
    }

    /**
     * Formats integer value for SQLite.
     *
     * @param value BigInteger value to format.
     *
     * @return Integer value in string formatted for SQLite.
     */
    public static String formatLiteral(BigInteger value)
    {
        // decimal integers are supported by SQLite in the same format
        return value.toString();
    }

    private final static String NAME = "INTEGER";
}
