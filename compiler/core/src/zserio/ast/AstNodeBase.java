package zserio.ast;

/**
 * Base implementation of AstNode interface.
 */
public abstract class AstNodeBase implements AstNode
{
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
