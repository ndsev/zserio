package zserio.ast;



/**
 * AST node for RPC calls.
 */
public class Rpc extends DocumentableAstNode
{
    /**
     * Constructor.
     *
     * @param location              AST node location.
     * @param name                  Name of the RPC call.
     * @param responseTypeReference Reference to the response type.
     * @param responseStreaming     True if response streaming is requested.
     * @param requestTypeReference           Reference to the request type.
     * @param requestStreaming      True if request streaming is requested.
     * @param docComment            Documentation comment belonging to this node.
     */
    public Rpc(AstLocation location, String name, TypeReference responseTypeReference,
            boolean responseStreaming, TypeReference requestTypeReference, boolean requestStreaming,
            DocComment docComment)
    {
        super(location, docComment);

        this.name = name;
        this.responseTypeReference = responseTypeReference;
        this.responseStreaming = responseStreaming;
        this.requestTypeReference = requestTypeReference;
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
        super.visitChildren(visitor);

        responseTypeReference.accept(visitor);
        requestTypeReference.accept(visitor);
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
    public CompoundType getRequestType()
    {
        return (CompoundType)requestTypeReference.getBaseType();
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
    public CompoundType getResponseType()
    {
        return (CompoundType)responseTypeReference.getBaseType();
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
     * Checks the RPC call.
     */
    void check()
    {
        checkUsedType(responseTypeReference);
        checkUsedType(requestTypeReference);
    }

    private void checkUsedType(TypeReference typeReference)
    {
        final ZserioType referencedBaseType = typeReference.getBaseType();
        if (!(referencedBaseType instanceof CompoundType))
        {
            throw new ParserException(typeReference,
                    "Only non-parameterized compound types can be used in RPC calls, " +
                    "'" + typeReference.getReferencedTypeName() + "' is not a compound type!");
        }

        final CompoundType compoundType = (CompoundType)referencedBaseType;
        if (compoundType.getTypeParameters().size() > 0)
        {
            throw new ParserException(typeReference,
                    "Only non-parameterized compound types can be used in RPC calls, '" +
                    ZserioTypeUtil.getReferencedFullName(typeReference) + "' is a parameterized type!");
        }
        if (compoundType instanceof SqlTableType)
            throw new ParserException(typeReference, "SQL table '" +
                    ZserioTypeUtil.getReferencedFullName(typeReference) + "' cannot be used in RPC call");
    }

    private final String name;
    private final TypeReference responseTypeReference;
    private final boolean responseStreaming;
    private final TypeReference requestTypeReference;
    private final boolean requestStreaming;
}
