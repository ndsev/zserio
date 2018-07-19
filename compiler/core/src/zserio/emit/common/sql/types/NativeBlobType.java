package zserio.emit.common.sql.types;

public class NativeBlobType implements SqlNativeType
{
    public NativeBlobType()
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

    private final static String NAME = "BLOB";
}
