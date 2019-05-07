package zserio.ast;

/**
 * Text element documentation node.
 */
public class DocTextElement extends AstNodeBase
{
    /**
     * Constructor.
     *
     * @param location  AST node location.
     * @param text  Text content.
     */
    public DocTextElement(AstLocation location, String text)
    {
        super(location);

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