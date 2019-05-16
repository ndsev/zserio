package zserio.ast;

public class DocElement extends AstNodeBase
{
    public DocElement(AstLocation location, DocTagSee seeTag)
    {
        this(location, null, seeTag, null, null);
    }

    public DocElement(AstLocation location, DocTagTodo todoTag)
    {
        this(location, null, null, todoTag, null);
    }

    public DocElement(AstLocation location, DocTagParam paramTag)
    {
        this(location, null, null, null, paramTag);
    }

    public DocElement(AstLocation location, DocMultiline docMultiline)
    {
        this(location, docMultiline, null, null, null);
    }

    @Override
    public void accept(ZserioAstVisitor visitor)
    {
        visitor.visitDocElement(this);
    }

    @Override
    public void visitChildren(ZserioAstVisitor visitor)
    {
        if (docMultiline != null)
            docMultiline.accept(visitor);

        if (seeTag != null)
            seeTag.accept(visitor);

        if (todoTag != null)
            todoTag.accept(visitor);

        if (paramTag != null)
            paramTag.accept(visitor);
    }

    public DocMultiline getDocMultiline()
    {
        return docMultiline;
    }

    public DocTagSee getSeeTag()
    {
        return seeTag;
    }

    public DocTagTodo getTodoTag()
    {
        return todoTag;
    }

    public DocTagParam getParamTag()
    {
        return paramTag;
    }

    private DocElement(AstLocation location, DocMultiline docMultiline, DocTagSee seeTag, DocTagTodo todoTag,
            DocTagParam paramTag)
    {
        super(location);

        this.docMultiline = docMultiline;
        this.seeTag = seeTag;
        this.todoTag = todoTag;
        this.paramTag = paramTag;
    }

    private final DocMultiline docMultiline;
    private final DocTagSee seeTag;
    private final DocTagTodo todoTag;
    private final DocTagParam paramTag;
}