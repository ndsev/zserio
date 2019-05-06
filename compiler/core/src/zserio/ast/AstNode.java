package zserio.ast;

/**
 * Interface for a single AST node.
 */
public interface AstNode
{
    /**
     * Gets the location of this node.
     *
     * @return Location of the current AST node.
     */
    public AstLocation getLocation();

    /**
     * Accept zserio visitor.
     *
     * @param visitor Visitor to accept.
     */
    public void accept(ZserioAstVisitor visitor);

    /**
     * Visit children of the current AST node using given visitor.
     *
     * @param visitor Visitor to use.
     */
    public void visitChildren(ZserioAstVisitor vistor);
}
