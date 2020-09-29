package zserio.ast;

import java.util.Collections;
import java.util.List;

/**
 * AST node which can have documentation comment.
 */
public abstract class DocumentableAstNode extends AstNodeBase
{
    /**
     * Constructor.
     *
     * @param location    AST node location.
     * @param docComments List of documentation comments belonging to this node.
     */
    public DocumentableAstNode(AstLocation location, List<DocComment> docComments)
    {
        super(location);

        this.docComments = docComments;
    }

    @Override
    public void visitChildren(ZserioAstVisitor visitor)
    {
        for (DocComment docComment : docComments)
            docComment.accept(visitor);
    }

    /**
     * Gets documentation comments belonging to this node.
     *
     * @return List of documentation comments.
     */
    public List<DocComment> getDocComments()
    {
        return Collections.unmodifiableList(docComments);
    }

    private final List<DocComment> docComments;
}
