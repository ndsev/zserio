package zserio.ast;

/**
 * AST node which can have documentation comment.
 */
public abstract class DocumentableAstNode extends AstNodeBase
{
    /**
     * Constructor.
     *
     * @param location   AST node location.
     * @param docComment Documentation comment belonging to this node.
     */
    public DocumentableAstNode(AstLocation location, DocComment docComment)
    {
        super(location);

        this.docComment = docComment;
    }

    @Override
    public void visitChildren(ZserioAstVisitor visitor)
    {
        if (docComment != null)
            docComment.accept(visitor);
    }

    /**
     * Gets documentation comment belonging to this node.
     *
     * @return Documentation comment.
     */
    public DocComment getDocComment()
    {
        return docComment;
    }

    private final DocComment docComment;
}