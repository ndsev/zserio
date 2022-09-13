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
     */
    public ItemInfo(String schemaName, BigInteger value)
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
     * @return Item value.
     */
    public BigInteger getValue()
    {
        return value;
    }

    private final String schemaName;
    private final BigInteger value;
}
