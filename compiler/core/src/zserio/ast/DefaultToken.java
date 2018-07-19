package zserio.ast;

import antlr.Token;

/**
 * The representation of default AST node which does not need any special handling.
 */
public class DefaultToken extends TokenAST
{
    /**
     *  Empty constructor.
     */
    public DefaultToken()
    {
        super();
    }

    /**
     * Constructor from ANTLR token.
     *
     * @param token ANTLR token to construct from.
     */
    public DefaultToken(Token token)
    {
        super(token);
    }

    private static final long serialVersionUID = -1L;
}
