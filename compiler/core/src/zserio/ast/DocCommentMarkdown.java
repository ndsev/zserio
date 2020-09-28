package zserio.ast;

/**
 * Class representing a single documentation comment in markdown style.
 */
public class DocCommentMarkdown extends DocComment
{
    /**
     * Constructor.
     *
     * @param location AST node location.
     * @param markdown Markdown documentation.
     */
    public DocCommentMarkdown(AstLocation location, String markdown)
    {
        super(location);

        this.markdown = markdown;
    }

    @Override
    public void accept(ZserioAstVisitor visitor)
    {
        visitor.visitDocCommentMarkdown(this);
    }

    @Override
    public void visitChildren(ZserioAstVisitor visitor)
    {
    }

    /**
     * Gets markdown documentation.
     *
     * @return Markdown documentation.
     */
    public String getMarkdown()
    {
        return markdown;
    }

    final String markdown;
}