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
     * Walks the underlying AST using the given listener.
     *
     * @param listener Listener to use for walking.
     */
    public void walk(ZserioListener listener);
}