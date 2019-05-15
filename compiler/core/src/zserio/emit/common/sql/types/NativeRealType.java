package zserio.emit.common.sql.types;

/**
 * SQLite native type for Floats.
 */
public class NativeRealType implements SqlNativeType
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
     * Formats float value in string format for SQLite.
     *
     * @param value Float value to format.
     *
     * @return Float value in string formatted for SQLite.
     */
    public static String formatLiteral(String value)
    {
        // floats are supported by SQLite in the same format
        return value;
    }

    private final static String NAME = "REAL";
}
