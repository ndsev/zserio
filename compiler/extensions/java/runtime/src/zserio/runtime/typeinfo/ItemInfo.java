package zserio.runtime.typeinfo;

import java.math.BigInteger;

/**
 * Type information for enumeration type item or for bitmask type value.
 */
public class ItemInfo
{
    /**
     * Constructor.
     *
     * @param schemaName Item schema name.
     * @param value Item value.
     * @param isDeprecated Flag whether the item is deprecated.
     * @param isRemoved Flag whether the item is removed.
     */
    public ItemInfo(String schemaName, BigInteger value, boolean isDeprecated, boolean isRemoved)
    {
        this.schemaName = schemaName;
        this.value = value;
        this.isDeprecated = isDeprecated;
        this.isRemoved = isRemoved;
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
     * @return Item value.
     */
    public BigInteger getValue()
    {
        return value;
    }

    /**
     * Gets flag whether the item is deprecated.
     *
     * @return True when the item is deprecated, false otherwise.
     */
    public boolean isDeprecated()
    {
        return isDeprecated;
    }

    /**
     * Gets flag whether the item is removed.
     *
     * @return True when the item is removed, false otherwise.
     */
    public boolean isRemoved()
    {
        return isRemoved;
    }

    private final String schemaName;
    private final BigInteger value;
    private final boolean isDeprecated;
    private final boolean isRemoved;
}
