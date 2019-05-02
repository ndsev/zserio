package zserio.ast4.doc;

import org.antlr.v4.runtime.Token;

import zserio.ast4.AstNodeBase;
import zserio.ast4.ZserioAstVisitor;

public class DocText extends AstNodeBase
{
    public DocText(Token token, DocTextElement textElement)
    {
        super(token);

        this.textElement = textElement;
        this.seeTag = null;
    }

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

    public DocTextElement getTextElement()
    {
        return textElement;
    }

    public DocTagSee getSeeTag()
    {
        return seeTag;
    }

    private final DocTextElement textElement;
    private final DocTagSee seeTag;
};
