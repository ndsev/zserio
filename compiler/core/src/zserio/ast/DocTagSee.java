package zserio.ast;

import zserio.antlr.util.ParserException;
import zserio.tools.ZserioToolPrinter;

/**
 * See tag documentation node.
 */
public class DocTagSee extends AstNodeBase
{
    /**
     * Constructor.
     *
     * @param location  Location of this AST node.
     * @param linkAlias Link alias. Defaults to link name if null is passed.
     * @param linkName  Link name.
     */
    public DocTagSee(AstLocation location, String linkAlias, String linkName)
    {
        super(location);

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
     *
     * @param ownerPackage Zserio package in which the symbol reference is defined.
     * @param ownerType ZserioType which is owner of the symbol reference.
     */
    void resolve(Package ownerPackage, ZserioScopedType ownerType)
    {
        try
        {
            linkSymbolReference.resolve(ownerPackage, ownerType);
        }
        catch (ParserException e)
        {
            ZserioToolPrinter.printWarning(e.getLocation(), "Documentation: " + e.getMessage());
        }
    }

    private final String linkAlias;
    private final SymbolReference linkSymbolReference;
}