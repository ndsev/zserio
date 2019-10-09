package zserio.ast;

import java.util.Collections;
import java.util.List;

/**
 * Class representing a single documentation comment.
 */
public class DocComment extends AstNodeBase
{
    /**
     * Constructor.
     *
     * @param location     AST node location.
     * @param paragraphs   Doc comment paragraphs.
     */
    public DocComment(AstLocation location, List<DocParagraph> paragraphs)
    {
        super(location);

        this.paragraphs = paragraphs;
    }

    @Override
    public void accept(ZserioAstVisitor visitor)
    {
        visitor.visitDocComment(this);
    }

    @Override
    public void visitChildren(ZserioAstVisitor visitor)
    {
        for (DocParagraph paragraph : paragraphs)
            paragraph.accept(visitor);
    }

    /**
     * Gets doc comment paragraphs.
     *
     * @return List of paragraphs.
     */
    public List<DocParagraph> getParagraphs()
    {
        return Collections.unmodifiableList(paragraphs);
    }

    private final List<DocParagraph> paragraphs;
}