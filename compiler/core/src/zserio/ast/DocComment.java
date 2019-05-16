package zserio.ast;

import java.util.Collections;
import java.util.List;

import org.antlr.v4.runtime.Token;

/**
 * Class representing a single documentation comment.
 */
public class DocComment extends AstNodeBase
{
    /**
     * Constructor.
     *
     * @param token        ANTLR4 token to localize AST node in the sources.
     * @param paragraphs   Doc comment paragraphs.
     * @param isDeprecated Flag if the documented node is deprecated.
     */
    public DocComment(Token token, List<DocParagraph> paragraphs, boolean isDeprecated)
    {
        super(token);

        this.paragraphs = paragraphs;
        this.isDeprecated = isDeprecated;
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

    /**
     * Gets whether the documented node is deprecated.
     *
     * @return Deprecation flag.
     */
    public boolean isDeprecated()
    {
        return isDeprecated;
    }

    private final List<DocParagraph> paragraphs;
    private final boolean isDeprecated;
}