package zserio.ast;

import java.util.Collections;
import java.util.List;

/**
 * Class representing a single documentation comment in classic style.
 */
public class DocCommentClassic extends DocComment
{
    /**
     * Constructor.
     *
     * @param location AST node location.
     * @param paragraphs Doc comment paragraphs.
     * @param isSticky True if the classic documentation comment is not followed by blank line.
     * @param isOneLiner True if the documentation comment is on one line in the source.
     */
    public DocCommentClassic(AstLocation location, List<DocParagraph> paragraphs, boolean isSticky,
            boolean isOneLiner)
    {
        super(location, isSticky, isOneLiner);

        this.paragraphs = paragraphs;
    }

    @Override
    public void accept(ZserioAstVisitor visitor)
    {
        visitor.visitDocCommentClassic(this);
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