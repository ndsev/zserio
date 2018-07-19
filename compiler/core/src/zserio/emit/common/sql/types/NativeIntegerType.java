package zserio.emit.common.sql.types;

import java.math.BigInteger;

public class NativeIntegerType implements SqlNativeType
{
    public NativeIntegerType()
    {}

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

    public static String formatLiteral(BigInteger value)
    {
        // decimal integers are supported by SQLite in the same format
        return value.toString();
    }

    private final static String NAME = "INTEGER";
}
