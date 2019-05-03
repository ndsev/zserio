package zserio.ast4;

import org.antlr.v4.runtime.Token;

/** See tag documentation node. */
public class DocTagSee extends AstNodeBase
{
    /**
     * Constructor.
     *
     * @param token     ANTLR4 token to localize AST node in the sources.
     * @param linkAlias Link alias. Defaults to link name if null is passed.
     * @param linkName  Link name.
     */
    public DocTagSee(Token token, String linkAlias, String linkName)
    {
        super(token);

        // link alias is the same as a link name if no alias is available
        this.linkAlias = linkAlias != null ? linkAlias : linkName;
        this.linkSymbolReference = new SymbolReference(this, linkName);
    }

    @Override
    public void accept(ZserioAstVisitor visitor)
    {
        visitor.visitDocTagSee(this);
    }

    /**
     * Gets see tag link alias.
     *
     * @return Link alias.
     */
    public String getLinkAlias()
    {
        return linkAlias;
    }

    /**
     * Gets reference to symbol which see tag points to.
     *
     * @return Symbol reference.
     */
    public SymbolReference getLinkSymbolReference()
    {
        return linkSymbolReference;
    }

    /**
     * Resolves the link symbol reference.
     */
    protected void resolve(Package ownerPackage, ZserioScopedType ownerType)
    {
        linkSymbolReference.resolve(ownerPackage, ownerType);
    }

    private final String linkAlias;
    private final SymbolReference linkSymbolReference;
}