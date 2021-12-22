package zserio.runtime.typeinfo;

/**
 * Type information for compound type function.
 */
public class FunctionInfo
{
    /**
     * Constructor.
     *
     * @param schemaName Function schema name.
     * @param typeInfo Function type info.
     * @param functionResult Function result expression.
     */
    public FunctionInfo(String schemaName, TypeInfo typeInfo, String functionResult)
    {
        this.schemaName = schemaName;
        this.typeInfo = typeInfo;
        this.functionResult = functionResult;
    }

    /**
     * Gets function schema name.
     *
     * @return Name of the function as is defined in zserio schema.
     */
    public String getSchemaName()
    {
        return schemaName;
    }

    /**
     * Gets type information for the function.
     *
     * @return Function type info.
     */
    public TypeInfo getTypeInfo()
    {
        return typeInfo;
    }

    /**
     * Gets result expression.
     *
     * @return Function result expression.
     */
    public String getFunctionResult()
    {
        return functionResult;
    }

    private final String schemaName;
    private final TypeInfo typeInfo;
    private final String functionResult;
}
