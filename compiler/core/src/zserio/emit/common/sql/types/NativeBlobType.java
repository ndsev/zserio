package zserio.emit.common.sql.types;

/**
 * SQLite native type for Blobs.
 */
public class NativeBlobType implements SqlNativeType
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

    private final static String NAME = "BLOB";
}
