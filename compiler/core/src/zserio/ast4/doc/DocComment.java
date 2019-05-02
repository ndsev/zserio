package zserio.ast4.doc;

import java.util.List;

import org.antlr.v4.runtime.Token;
import zserio.ast4.AstNodeBase;
import zserio.ast4.ZserioAstVisitor;

public class DocComment extends AstNodeBase
{
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

    public Iterable<DocParagraph> getParagraphs()
    {
        return paragraphs;
    }

    public Iterable<DocTagSee> getSeeTags()
    {
        return seeTags;
    }

    public Iterable<DocTagTodo> getTodoTags()
    {
        return todoTags;
    }

    public Iterable<DocTagParam> getParamTags()
    {
        return paramTags;
    }

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