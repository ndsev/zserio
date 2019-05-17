package zserio.ast;

/**
 * Documentation node which wraps documentation element which can be either a text or a tag.
 */
public class DocElement extends AstNodeBase
{
    /**
     * Constructor from multiline.
     *
     * @param location      Location of this AST node.
     * @param docMultiline  Multiline AST node.
     */
    public DocElement(AstLocation location, DocMultiline docMultiline)
    {
        this(location, docMultiline, null, null, null, null);
    }

    /**
     * Constructor from see tag.
     *
     * @param location  ANTLR4 token to localize AST node in the sources.
     * @param seeTag    See tag.
     */
    public DocElement(AstLocation location, DocTagSee seeTag)
    {
        this(location, null, seeTag, null, null, null);
    }

    /**
     * Constructor from todo tag.
     *
     * @param location  ANTLR4 token to localize AST node in the sources.
     * @param todoTag   Todo tag.
     */
    public DocElement(AstLocation location, DocTagTodo todoTag)
    {
        this(location, null, null, todoTag, null, null);
    }

    /**
     * Constructor from param tag.
     *
     * @param location  ANTLR4 token to localize AST node in the sources.
     * @param paramTag   Param tag.
     */
    public DocElement(AstLocation location, DocTagParam paramTag)
    {
        this(location, null, null, null, paramTag, null);
    }

    /**
     * Constructor from deprecated tag.
     *
     * @param location      ANTLR4 token to localize AST node in the sources.
     * @param deprecatedTag Deprecated tag.
     */
    public DocElement(AstLocation location, DocTagDeprecated deprecatedTag)
    {
        this(location, null, null, null, null, deprecatedTag);
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

        if (deprecatedTag != null)
            deprecatedTag.accept(visitor);
    }

    /**
     * Gets documentation multiline text if available.
     *
     * @return Multiline text or null.
     */
    public DocMultiline getDocMultiline()
    {
        return docMultiline;
    }

    /**
     * Gets documentation see tag if available.
     *
     * @return See tag or null.
     */
    public DocTagSee getSeeTag()
    {
        return seeTag;
    }

    /**
     * Gets documentation todo tag if available.
     *
     * @return Todo tag or null.
     */
    public DocTagTodo getTodoTag()
    {
        return todoTag;
    }

    /**
     * Gets documentation param tag if available.
     *
     * @return Param tag or null.
     */
    public DocTagParam getParamTag()
    {
        return paramTag;
    }

    /**
     * Gets documentation deprecated tag if available.
     *
     * @return Deprecated tag or null.
     */
    public DocTagDeprecated getDeprecatedTag()
    {
        return deprecatedTag;
    }

    private DocElement(AstLocation location, DocMultiline docMultiline, DocTagSee seeTag, DocTagTodo todoTag,
            DocTagParam paramTag, DocTagDeprecated deprecatedTag)
    {
        super(location);

        this.docMultiline = docMultiline;
        this.seeTag = seeTag;
        this.todoTag = todoTag;
        this.paramTag = paramTag;
        this.deprecatedTag = deprecatedTag;
    }

    private final DocMultiline docMultiline;
    private final DocTagSee seeTag;
    private final DocTagTodo todoTag;
    private final DocTagParam paramTag;
    private final DocTagDeprecated deprecatedTag;
}