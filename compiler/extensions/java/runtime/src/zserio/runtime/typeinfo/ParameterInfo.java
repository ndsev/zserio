package zserio.runtime.typeinfo;

/**
 * Type information for compound type parameter.
 */
public final class ParameterInfo
{
    /**
     * Constructor.
     *
     * @param schemaName Parameter schema name.
     * @param typeInfo Parameter type info.
     */
    public ParameterInfo(String schemaName, TypeInfo typeInfo)
    {
        this.schemaName = schemaName;
        this.typeInfo = typeInfo;
    }

    /**
     * Gets parameter schema name.
     *
     * @return Name of the parameter as is defined in zserio schema.
     */
    public String getSchemaName()
    {
        return schemaName;
    }

    /**
     * Gets type information for the parameter.
     *
     * @return Parameter type info.
     */
    public TypeInfo getTypeInfo()
    {
        return typeInfo;
    }

    private final String schemaName;
    private final TypeInfo typeInfo;
}