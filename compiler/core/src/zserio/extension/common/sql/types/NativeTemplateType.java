package zserio.extension.common.sql.types;

/**
 * SQLite native type for types dependent on template parameters.
 */
public final class NativeTemplateType implements SqlNativeType
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

    private final static String NAME = "TEMPLATE_PARAMETER";
}
