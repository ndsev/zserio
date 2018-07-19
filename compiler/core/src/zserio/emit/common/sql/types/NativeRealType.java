package zserio.emit.common.sql.types;

public class NativeRealType implements SqlNativeType
{
    public NativeRealType()
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

    public static String formatLiteral(String value)
    {
        // floats are supported by SQLite in the same format
        return value;
    }

    private final static String NAME = "REAL";
}
