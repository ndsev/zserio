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
     * @param isSticky True if the Markdown documentation comment is not followed by blank line.
     * @param isOneLiner True if the documentation comment is on one line in the source.
     */
    public DocCommentMarkdown(AstLocation location, String markdown, boolean isSticky, boolean isOneLiner)
    {
        super(location, isSticky, isOneLiner);

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