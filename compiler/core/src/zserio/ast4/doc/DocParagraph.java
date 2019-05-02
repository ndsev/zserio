package zserio.ast4.doc;

import org.antlr.v4.runtime.Token;

import zserio.ast4.ZserioAstVisitor;

public class DocParagraph extends DocMultilineNode
{
    public DocParagraph(Token token, DocTextLine firstLine)
    {
        super(token, firstLine);
    }

    public void accept(ZserioAstVisitor visitor)
    {
        visitor.visitDocParagraph(this);
    }
}