package zserio.ast4.doc;

import org.antlr.v4.runtime.Token;

import zserio.ast4.ZserioAstVisitor;

public class DocTagTodo extends DocMultilineNode
{
    public DocTagTodo(Token token, DocTextLine firstLine)
    {
        super(token, firstLine);
    }

    public void accept(ZserioAstVisitor visitor)
    {
        visitor.visitDocTagTodo(this);
    }
}
