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
     */
    public DocComment(Token token, List<DocParagraph> paragraphs)
    {
        super(token);

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