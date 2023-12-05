package zserio.runtime.typeinfo;

/**
 * Type information for SQL database table.
 */
public final class TableInfo
{
    /**
     * Constructor.
     *
     * @param schemaName Table schema name.
     * @param typeInfo Table type info.
     */
    public TableInfo(String schemaName, TypeInfo typeInfo)
    {
        this.schemaName = schemaName;
        this.typeInfo = typeInfo;
    }

    /**
     * Gets name of the table as is defined in zserio schema.
     *
     * @return Table schema name.
     */
    public String getSchemaName()
    {
        return schemaName;
    }

    /**
     * Gets type information for the table.
     *
     * @return Table type info.
     */
    public TypeInfo getTypeInfo()
    {
        return typeInfo;
    }

    private final String schemaName;
    private final TypeInfo typeInfo;
}
