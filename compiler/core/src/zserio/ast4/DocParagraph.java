package zserio.ast4;

import org.antlr.v4.runtime.Token;

/**
 * Documentation paragraph.
 */
public class DocParagraph extends DocMultilineNode
{
    /**
     * Constructor.
     *
     * @param token     ANTLR4 token to localize AST node in the sources.
     * @param firstLine First documentation line.
     */
    public DocParagraph(Token token, DocTextLine firstLine)
    {
        super(token, firstLine);
    }

    @Override
    public void accept(ZserioAstVisitor visitor)
    {
        visitor.visitDocParagraph(this);
    }
}