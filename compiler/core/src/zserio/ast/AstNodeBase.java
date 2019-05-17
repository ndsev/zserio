package zserio.ast;

import org.antlr.v4.runtime.Token;

/**
 * Base implementation of AstNode interface.
 */
public abstract class AstNodeBase implements AstNode
{
    /**
     * Constructor from ANTLR4 token.
     *
     * @param token Token to construct from.
     */
    public AstNodeBase(Token token)
    {
        this.location = new AstLocation(token);
    }

    /**
     * Constructor from AST location.
     *
     * @param location AST node location.
     */
    public AstNodeBase(AstLocation location)
    {
        this.location = location;
    }

    @Override
    public AstLocation getLocation()
    {
        return location;
    }

    @Override
    public void visitChildren(ZserioAstVisitor visitor)
    {}

    private final AstLocation location;
};
