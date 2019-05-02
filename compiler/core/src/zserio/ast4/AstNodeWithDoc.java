package zserio.ast4;

import org.antlr.v4.runtime.Token;

import zserio.ast4.doc.DocComment;

public abstract class AstNodeWithDoc extends AstNodeBase
{
    /**
     * Constructor.
     *
     * @param token Token to construct from.
     * @param docComment Documentation comment belonging to this node.
     */
    public AstNodeWithDoc(Token token, DocComment docComment)
    {
        super(token);

        this.docComment = docComment;
    }

    @Override
    public void visitChildren(ZserioAstVisitor visitor)
    {
        if (docComment != null)
            visitor.visitDocComment(docComment);
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