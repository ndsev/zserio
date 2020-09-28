package zserio.ast;

/**
 * Base class representing a single documentation comment.
 */
public abstract class DocComment extends AstNodeBase
{
    /**
     * Constructor.
     *
     * @param location     AST node location.
     */
    public DocComment(AstLocation location)
    {
        super(location);
    }
}