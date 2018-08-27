package zserio.ast;

import zserio.antlr.ZserioParserTokenTypes;
import zserio.antlr.util.BaseTokenAST;
import zserio.antlr.util.ParserException;
import zserio.ast.doc.DocCommentToken;

/**
 * AST node for rpc calls.
 */
public class Rpc extends TokenAST
{
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
     * Gets response type of the RPC method.
     *
     * @return Response type of this RPC method.
     */
    public ZserioType getResponseType()
    {
        return responseType;
    }

    /**
     * Gets documentation comment associated to this RPC method.
     *
     * @return Documentation comment token associated to this RPC method.
     */
    public DocCommentToken getDocComment()
    {
        return getHiddenDocComment();
    }

    @Override
    protected boolean evaluateChild(BaseTokenAST child) throws ParserException
    {
        switch (child.getType())
        {
        case ZserioParserTokenTypes.ID:
            if (!(child instanceof IdToken))
                return false;
            name = child.getText();
            break;

        default:
            if (!(child instanceof ZserioType))
                return false;
            if (responseType == null)
                responseType = (ZserioType)child;
            else
                requestType = (ZserioType)child;
            break;
        }

        return true;
    }

    @Override
    protected void evaluate() throws ParserException
    {
        evaluateHiddenDocComment(serviceType);
    }

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
            throw new ParserException(this, "Only non-parameterized compound types can be used in RPC calls, " +
                    "'" + type.getName() + "' is not a compound type!");
        if (((CompoundType)resolvedBaseType).getParameters().size() > 0)
            throw new ParserException(this, "Only non-parameterized compound types can be used in RPC calls, " +
                    "'" + type.getName() + "' is a parameterized type!");
        if (resolvedBaseType instanceof SqlTableType)
            throw new ParserException(this, "SQL table '" + type.getName() + "' cannot be used in RPC call");
    }

    private static final long serialVersionUID = -5025876957933812510L;

    private String name;
    private ZserioType responseType;
    private ZserioType requestType;
    private ServiceType serviceType;
}
