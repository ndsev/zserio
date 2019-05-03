package zserio.ast4;

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
     * @param seeTags      Doc comment see tags.
     * @param todoTags     Doc comment todo tags.
     * @param paramTags    Doc comment param tags.
     * @param isDeprecated Flag if the documented node is deprecated.
     */
    public DocComment(Token token, List<DocParagraph> paragraphs, List<DocTagSee> seeTags,
            List<DocTagTodo> todoTags, List<DocTagParam> paramTags, boolean isDeprecated)
    {
        super(token);

        this.paragraphs = paragraphs;
        this.seeTags = seeTags;
        this.todoTags = todoTags;
        this.paramTags = paramTags;
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

        for (DocTagSee seeTag : seeTags)
            seeTag.accept(visitor);

        for (DocTagTodo todoTag : todoTags)
            todoTag.accept(visitor);

        for (DocTagParam paramTag : paramTags)
            paramTag.accept(visitor);
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
     * Gets doc comment see tags.
     *
     * @return List of see tags.
     */
    public List<DocTagSee> getSeeTags()
    {
        return Collections.unmodifiableList(seeTags);
    }

    /**
     * Gets doc comment todo tags.
     *
     * @return List of todo tags.
     */
    public List<DocTagTodo> getTodoTags()
    {
        return Collections.unmodifiableList(todoTags);
    }

    /**
     * Gets doc comment param tags.
     *
     * @return List of param tags.
     */
    public List<DocTagParam> getParamTags()
    {
        return Collections.unmodifiableList(paramTags);
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
    private final List<DocTagSee> seeTags;
    private final List<DocTagTodo> todoTags;
    private final List<DocTagParam> paramTags;
    private final boolean isDeprecated;
}