package zserio.runtime.typeinfo;

import java.util.function.Function;

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
     * @param functionResult Function result.
     */
    public FunctionInfo(String schemaName, TypeInfo typeInfo, Function<Object, Object> functionResult)
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
    public Function<Object, Object> getFunctionResult()
    {
        return functionResult;
    }

    private final String schemaName;
    private final TypeInfo typeInfo;
    private final Function<Object, Object> functionResult;
}
