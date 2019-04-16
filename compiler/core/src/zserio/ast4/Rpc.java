package zserio.ast4;

import org.antlr.v4.runtime.Token;

/**
 * AST node for rpc calls.
 */
public class Rpc extends AstNodeBase
{
    public Rpc(Token token, String name, ZserioType responseType, boolean responseStreaming,
            ZserioType requestType, boolean requestStreaming)
    {
        super(token);

        this.name = name;
        this.responseType = responseType;
        this.responseStreaming = responseStreaming;
        this.requestType = requestType;
        this.requestStreaming = requestStreaming;
    }

    @Override
    public void accept(ZserioAstVisitor visitor)
    {
        visitor.visitRpc(this);
    }

    @Override
    public void visitChildren(ZserioAstVisitor visitor)
    {
        responseType.accept(visitor);
        requestType.accept(visitor);
    }

    /**
     * Gets name of the RCP method.
     *
     * @return RPC method name.
     */
    public String getName()
    {
        return name;
    }

    /**
     * Gets request type of the RPC method.
     *
     * @return Request type of this RPC method.
     */
    public ZserioType getRequestType()
    {
        return requestType;
    }

    /**
     * Returns whether the request is defined as a stream.
     *
     * @return True when request is defined as a stream.
     */
    public boolean hasRequestStreaming()
    {
        return requestStreaming;
    }

    /**
     * Gets response type of the RPC method.
     *
     * @return Response type of this RPC method.
     */
    public ZserioType getResponseType()
    {
        return responseType;
    }

    /**
     * Returns whether the response is defined as a stream.
     *
     * @return True when response is defined as a stream.
     */
    public boolean hasResponseStreaming()
    {
        return responseStreaming;
    }

    /**
     * Gets documentation comment associated to this RPC method.
     *
     * @return Documentation comment token associated to this RPC method.
     */
    /*public DocCommentToken getDocComment()
    {
        return getHiddenDocComment();
    }*/ // TODO:

    @Override
    protected void check() throws ParserException
    {
        // fill used type list
        checkUsedType(responseType);
        checkUsedType(requestType);
    }

    /**
     * Sets service type which is owner of this RPC method.
     *
     * @param serviceType Owner to set.
     */
    protected void setServiceType(ServiceType serviceType)
    {
        this.serviceType = serviceType;
    }

    private void checkUsedType(ZserioType type) throws ParserException
    {
        final ZserioType resolvedBaseType = TypeReference.resolveBaseType(type);
        if (!(resolvedBaseType instanceof CompoundType))
            throw new ParserException(type, "Only non-parameterized compound types can be used in RPC calls, " +
                    "'" + type.getName() + "' is not a compound type!");

        final CompoundType compoundType = (CompoundType)resolvedBaseType;
        if (compoundType.getParameters().size() > 0)
            throw new ParserException(type, "Only non-parameterized compound types can be used in RPC calls, " +
                    "'" + type.getName() + "' is a parameterized type!");
        if (resolvedBaseType instanceof SqlTableType)
            throw new ParserException(type, "SQL table '" + type.getName() + "' cannot be used in RPC call");

        /*compoundType.setUsedByServiceType(serviceType);
        usedTypeList.add(TypeReference.resolveType(type));*/ // TODO:
    }

    private final String name;
    private final ZserioType responseType;
    private boolean responseStreaming;
    private final ZserioType requestType;
    private final boolean requestStreaming;
    private ServiceType serviceType = null;
}
