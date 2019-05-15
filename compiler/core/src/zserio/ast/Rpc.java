package zserio.ast;

import org.antlr.v4.runtime.Token;

import zserio.antlr.util.ParserException;


/**
 * AST node for RPC calls.
 */
public class Rpc extends AstNodeWithDoc
{
    /**
     * Constructor.
     *
     * @param token             ANTLR4 token to localize AST node in the sources.
     * @param name              Name of the RPC call.
     * @param responseType      Zserio type of the response.
     * @param responseStreaming True if response streaming is requested.
     * @param requestType       Zserio type of the request.
     * @param requestStreaming  True if request streaming is requested.
     * @param docComment        Documentation comment belonging to this node.
     */
    public Rpc(Token token, String name, ZserioType responseType, boolean responseStreaming,
            ZserioType requestType, boolean requestStreaming, DocComment docComment)
    {
        super(token, docComment);

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
     * Checks the RPC call.
     */
    void check()
    {
        checkUsedType(responseType);
        checkUsedType(requestType);
    }

    private void checkUsedType(ZserioType type)
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
    }

    private final String name;
    private final ZserioType responseType;
    private final boolean responseStreaming;
    private final ZserioType requestType;
    private final boolean requestStreaming;
}
