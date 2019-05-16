package zserio.ast;

/**
 * Todo tag documentation node.
 */
public class DocTagTodo extends DocMultiline
{
    /**
     * Constructor.
     *
     * @param location  AST node location.
     * @param firstLine First line of todo description.
     */
    public DocTagTodo(AstLocation location, DocLine firstLine)
    {
        super(location, firstLine);
    }

    @Override
    public void accept(ZserioAstVisitor visitor)
    {
        visitor.visitDocTagTodo(this);
    }
}
