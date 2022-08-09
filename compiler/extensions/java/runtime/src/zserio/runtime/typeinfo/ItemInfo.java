package zserio.runtime.typeinfo;

import java.util.function.Supplier;

/**
 * Type information for enumeration type item or for bitmask type value.
 */
public class ItemInfo
{
    /**
     * Constructor.
     *
     * @param schemaName Item schema name.
     * @param value Item value expression.
     */
    public ItemInfo(String schemaName, Supplier<Number> value)
    {
        this.schemaName = schemaName;
        this.value = value;
    }

    /**
     * Gets enumeration item or bitmask value schema name.
     *
     * @return Item schema name.
     */
    public String getSchemaName()
    {
        return schemaName;
    }

    /**
     * Gets enumeration item value or bitmask value.
     *
     * @return Item value expression.
     */
    public Supplier<Number> getValue()
    {
        return value;
    }

    private final String schemaName;
    private final Supplier<Number> value;
}
