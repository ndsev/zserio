package zserio.ast;

import zserio.antlr.util.ParserException;


/**
 * AST node for RPC calls.
 */
public class Rpc extends AstNodeWithDoc
{
    /**
     * Constructor.
     *
     * @param location          AST node location.
     * @param name              Name of the RPC call.
     * @param responseType      Zserio type of the response.
     * @param responseStreaming True if response streaming is requested.
     * @param requestType       Zserio type of the request.
     * @param requestStreaming  True if request streaming is requested.
     * @param docComment        Documentation comment belonging to this node.
     */
    public Rpc(AstLocation location, String name, ZserioType responseType, boolean responseStreaming,
            ZserioType requestType, boolean requestStreaming, DocComment docComment)
    {
        super(location, docComment);

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

        super.visitChildren(visitor);
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
        return requestCompoundType;
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
        return responseCompoundType;
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
     * Evaluates the RPC method.
     */
    void evaluate()
    {
        responseCompoundType = resolveCompoundType(responseType);
        requestCompoundType = resolveCompoundType(requestType);
    }

    /**
     * Checks the RPC call.
     */
    void check()
    {
        checkUsedType(responseType, responseCompoundType);
        checkUsedType(requestType, requestCompoundType);
    }

    private void checkUsedType(ZserioType type, CompoundType compoundType)
    {
        if (compoundType.getParameters().size() > 0)
            throw new ParserException(type, "Only non-parameterized compound types can be used in RPC calls, " +
                    "'" + type.getName() + "' is a parameterized type!");

        if (compoundType instanceof SqlTableType)
            throw new ParserException(type, "SQL table '" + type.getName() + "' cannot be used in RPC call");
    }

    private CompoundType resolveCompoundType(ZserioType type)
    {
        final ZserioType resolvedBaseType = TypeReference.resolveBaseType(type);

        if (!(resolvedBaseType instanceof CompoundType))
            throw new ParserException(type, "Only non-parameterized compound types can be used in RPC calls, " +
                    "'" + type.getName() + "' is not a compound type!");

        return (CompoundType)resolvedBaseType;
    }

    private final String name;
    private final ZserioType responseType;
    private final boolean responseStreaming;
    private final ZserioType requestType;
    private final boolean requestStreaming;

    private CompoundType responseCompoundType = null;
    private CompoundType requestCompoundType = null;
}
