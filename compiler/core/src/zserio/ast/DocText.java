package zserio.ast;

/**
 * Documentation text node.
 */
public class DocText extends AstNodeBase
{
    /**
     * Constructor.
     *
     * @param location  AST node location.
     * @param text      Text content.
     */
    public DocText(AstLocation location, String text)
    {
        super(location);

        this.text = text;
    }

    @Override
    public void accept(ZserioAstVisitor visitor)
    {
        visitor.visitDocText(this);
    }

    /**
     * Gets text string.
     *
     * @return Text content.
     */
    public String getText()
    {
        return text;
    }

    private final String text;
}