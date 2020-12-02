package zserio.ast;

/**
 * Base class representing a single documentation comment.
 */
public abstract class DocComment extends AstNodeBase
{
    /**
     * Constructor.
     *
     * @param location AST node location.
     * @param isSticky True if the documentation comment is not followed by blank line.
     * @param isOneLiner True if the documentation comment is on one line in the source.
     */
    public DocComment(AstLocation location, boolean isSticky, boolean isOneLiner)
    {
        super(location);

        this.isSticky = isSticky;
        this.isOneLiner = isOneLiner;
    }

    /**
     * Checks if the documentation comment is sticky.
     *
     * @return True if the documentation comment is not followed by blank line.
     */
    public boolean isSticky()
    {
        return isSticky;
    }

    /**
     * Checks if the documentation comment is one-liner.
     *
     * @return True if the documentation comment is on one line in the source.
     */
    public boolean isOneLiner()
    {
        return isOneLiner;
    }

    private final boolean isSticky;
    private final boolean isOneLiner;
}
