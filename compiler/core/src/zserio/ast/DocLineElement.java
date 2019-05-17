package zserio.ast;

/**
 * Documentation node which wraps documentation line element which can be either DocText or DocTagSee.
 */
public class DocLineElement extends AstNodeBase
{
    /**
     * Constructor from text element.
     *
     * @param location  Location of this AST node.
     * @param docText   Documentation text.
     */
    public DocLineElement(AstLocation location, DocText docText)
    {
        super(location);

        this.docText = docText;
        this.seeTag = null;
    }

    /**
     * Constructor from see tag.
     *
     * @param location AST node location.
     * @param seeTag   See tag.
     */
    public DocLineElement(AstLocation location, DocTagSee seeTag)
    {
        super(location);

        this.docText = null;
        this.seeTag = seeTag;
    }

    @Override
    public void accept(ZserioAstVisitor visitor)
    {
        visitor.visitDocLineElement(this);
    }

    @Override
    public void visitChildren(ZserioAstVisitor visitor)
    {
        if (docText != null)
            docText.accept(visitor);
        if (seeTag != null)
            seeTag.accept(visitor);
    }

    /**
     * Gets text element if available.
     *
     * @return Text element or null.
     */
    public DocText getDocText()
    {
        return docText;
    }

    /**
     * Gets see tag if available.
     *
     * @return See tag or null.
     */
    public DocTagSee getSeeTag()
    {
        return seeTag;
    }

    private final DocText docText;
    private final DocTagSee seeTag;
};
