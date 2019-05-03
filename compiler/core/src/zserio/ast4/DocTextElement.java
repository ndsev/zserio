package zserio.ast4;

import org.antlr.v4.runtime.Token;

/**
 * Text element documentation node.
 */
public class DocTextElement extends AstNodeBase
{
    /**
     * Constructor.
     *
     * @param token ANTLR4 token to localize AST node in the sources.
     * @param text  Text content.
     */
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

    /**
     * Gets text content of the current text element.
     *
     * @return Text content.
     */
    public String getText()
    {
        return text;
    }

    private final String text;
}