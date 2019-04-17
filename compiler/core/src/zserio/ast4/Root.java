package zserio.ast4;

import java.util.LinkedHashMap;
import java.util.Map;
import zserio.emit.common.Emitter4;
import zserio.emit.common.ZserioEmitException;

/**
 * The representation of AST ROOT node.
 *
 * This class represents an AST node which has all translation units compiled by zserio.
 */
public class Root extends AstNodeBase
{
    /**
     * Constructor.
     */
    public Root(LinkedHashMap<PackageName, Package> packageNameMap, boolean checkUnusedTypes)
    {
        super(null);

        this.packageNameMap = packageNameMap;
        this.checkUnusedTypes = checkUnusedTypes;
    }

    @Override
    public void accept(ZserioAstVisitor visitor)
    {
        visitor.visitRoot(this);
    }

    @Override
    public void visitChildren(ZserioAstVisitor visitor)
    {
        for (Package pkg : packageNameMap.values())
            pkg.accept(visitor);
    }

    /**
     * Gets map of all packages.
     */
    public Map<PackageName, Package> getPackageNameMap()
    {
        return packageNameMap;
    }

    /**
     * Walks through AST tree applying given emitter interface.
     *
     * @param emitter Emitter interface to use for walking.
     *
     * @throws ZserioEmitException Throws in case of unknown ZserioType.
     */
    public void walk(Emitter4 emitter) throws ZserioEmitException
    {
        final ZserioAstEmitter astEmitter = new ZserioAstEmitter(emitter);
        try
        {
            this.accept(astEmitter);
        }
        catch (ZserioAstEmitter.UncheckedZserioEmitException e)
        {
            throw e.getCause();
        }
    }

    protected void check()
    {
        // TODO: check for unused types - maybe just do it in self-standing visitor and don't use root for it?
    }

    private final LinkedHashMap<PackageName, Package> packageNameMap;
    private final boolean checkUnusedTypes;
}
