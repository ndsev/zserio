package zserio.ast4;

import org.antlr.v4.runtime.Token;

public abstract class AstNodeBase implements AstNode
{
    public AstNodeBase(Token token)
    {
        this.location = new AstNodeLocation(token);
    }

    @Override
    public AstNodeLocation getLocation()
    {
        return location;
    }

    @Override
    public void visitChildren(ZserioAstVisitor visitor)
    {}

    /**
     * Checks integrity of this node.
     *
     * This method is supposed to be overridden by inherited class.
     */
    protected void check()
    {}

    /**
     * Evaluates this node.
     *
     * This method is supposed to be overridden by inherited class.
     */
    protected void evaluate()
    {}

    private final AstNodeLocation location;
};
