package zserio.ast;

/**
 * Documentation paragraph.
 */
public class DocParagraph extends DocMultilineNode
{
    /**
     * Constructor.
     *
     * @param location  AST node location.
     * @param firstLine First documentation line.
     */
    public DocParagraph(AstLocation location, DocTextLine firstLine)
    {
        super(location, firstLine);
    }

    @Override
    public void accept(ZserioAstVisitor visitor)
    {
        visitor.visitDocParagraph(this);
    }
}