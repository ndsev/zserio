package zserio.ast4;

import org.antlr.v4.runtime.Token;

/**
 * Todo tag documentation node.
 */
public class DocTagTodo extends DocMultilineNode
{
    /**
     * Constructor.
     *
     * @param token     ANTLR4 token to localize AST node in the sources.
     * @param firstLine First line of todo description.
     */
    public DocTagTodo(Token token, DocTextLine firstLine)
    {
        super(token, firstLine);
    }

    @Override
    public void accept(ZserioAstVisitor visitor)
    {
        visitor.visitDocTagTodo(this);
    }
}
