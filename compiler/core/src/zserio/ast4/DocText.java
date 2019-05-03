package zserio.ast4;

import org.antlr.v4.runtime.Token;

/**
 * Documentation node which wraps documentation text which can be either DocTextElement or DocTagSee.
 */
public class DocText extends AstNodeBase
{
    /**
     * Constructor from text element.
     *
     * @param token       ANTLR4 token to localize AST node in the sources.
     * @param textElement Text element.
     */
    public DocText(Token token, DocTextElement textElement)
    {
        super(token);

        this.textElement = textElement;
        this.seeTag = null;
    }

    /**
     * Constructor from see tag.
     *
     * @param token  ANTLR4 token to localize AST node in the sources.
     * @param seeTag See tag.
     */
    public DocText(Token token, DocTagSee seeTag)
    {
        super(token);

        this.textElement = null;
        this.seeTag = seeTag;
    }

    @Override
    public void accept(ZserioAstVisitor visitor)
    {
        visitor.visitDocText(this);
    }

    @Override
    public void visitChildren(ZserioAstVisitor visitor)
    {
        if (textElement != null)
            textElement.accept(visitor);
        if (seeTag != null)
            seeTag.accept(visitor);
    }

    /**
     * Gets text element if available.
     *
     * @return Text element or null.
     */
    public DocTextElement getTextElement()
    {
        return textElement;
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

    private final DocTextElement textElement;
    private final DocTagSee seeTag;
};
