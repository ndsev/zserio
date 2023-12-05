package zserio.runtime.typeinfo;

/**
 * Type information for service method.
 */
public final class MethodInfo
{
    /**
     * Constructor.
     *
     * @param schemaName Method schema name.
     * @param responseTypeInfo Response type info.
     * @param requestTypeInfo Request type info.
     */
    public MethodInfo(String schemaName, TypeInfo responseTypeInfo, TypeInfo requestTypeInfo)
    {
        this.schemaName = schemaName;
        this.responseTypeInfo = responseTypeInfo;
        this.requestTypeInfo = requestTypeInfo;
    }

    /**
     * Gets name of the method as is defined in zserio schema.
     *
     * @return Service schema name.
     */
    public String getSchemaName()
    {
        return schemaName;
    }

    /**
     * Gets type information for the method response type.
     *
     * @return Response type info.
     */
    public TypeInfo getResponseTypeInfo()
    {
        return responseTypeInfo;
    }

    /**
     * Gets type information for the method request type.
     *
     * @return Request type info.
     */
    public TypeInfo getRequestTypeInfo()
    {
        return requestTypeInfo;
    }

    private final String schemaName;
    private final TypeInfo responseTypeInfo;
    private final TypeInfo requestTypeInfo;
}
