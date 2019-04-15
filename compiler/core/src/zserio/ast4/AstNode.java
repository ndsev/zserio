package zserio.ast4;

public interface AstNode
{
    /**
     * Gets the location of this node.
     *
     * @return Location of the current AST node.
     */
    public AstNodeLocation getLocation();

    /**
     * Accept zserio visitor.
     *
     * @param visitor Visitor to accept.
     */
    public void accept(ZserioVisitor visitor);

    /**
     * Visit children of the current AST node using given visitor.
     *
     * @param visitor Visitor to use.
     */
    public void visitChildren(ZserioVisitor vistor);
}
