package zserio.ast;

/** Deprecated tag documentation node. */
public class DocTagDeprecated extends AstNodeBase
{
    /**
     * Constructor.
     *
     * @param location  Location of this AST node.
     */
    public DocTagDeprecated(AstLocation location)
    {
        super(location);
    }

    @Override
    public void accept(ZserioAstVisitor visitor)
    {
        visitor.visitDocTagDeprecated(this);
    }
}