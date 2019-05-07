package zserio.ast;

/**
 * Todo tag documentation node.
 */
public class DocTagTodo extends DocMultilineNode
{
    /**
     * Constructor.
     *
     * @param location  AST node location.
     * @param firstLine First line of todo description.
     */
    public DocTagTodo(AstLocation location, DocTextLine firstLine)
    {
        super(location, firstLine);
    }

    @Override
    public void accept(ZserioAstVisitor visitor)
    {
        visitor.visitDocTagTodo(this);
    }
}
