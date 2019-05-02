package zserio.ast4.doc;

import org.antlr.v4.runtime.Token;

import zserio.ast4.AstNodeBase;
import zserio.ast4.ZserioAstVisitor;

public class DocTextElement extends AstNodeBase
{
    public DocTextElement(Token token, String text)
    {
        super(token);

        this.text = text;
    }

    @Override
    public void accept(ZserioAstVisitor visitor)
    {
        visitor.visitDocTextElement(this);
    }

    public String getText()
    {
        return text;
    }

    private final String text;
}