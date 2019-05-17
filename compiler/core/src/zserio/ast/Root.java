package zserio.ast;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import zserio.emit.common.Emitter;
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
     *
     * @param packageNameMap   Map of all available packages.
     */
    public Root(LinkedHashMap<PackageName, Package> packageNameMap)
    {
        super((AstLocation)null);

        this.packageNameMap = packageNameMap;
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
     * Walks through AST tree applying given emitter interface.
     *
     * @param emitter Emitter interface to use for walking.
     *
     * @throws ZserioEmitException Throws in case of any error.
     */
    public void emit(Emitter emitter) throws ZserioEmitException
    {
        final ZserioAstEmitter astEmitter = new ZserioAstEmitter(emitter);
        try
        {
            this.accept(astEmitter);
        }
        catch (ZserioAstEmitter.UncheckedZserioEmitException e)
        {
            throw e.getOriginalException();
        }
    }

    /**
     * Gets map of all packages.
     *
     * @return The map of all available packages.
     */
    Map<PackageName, Package> getPackageNameMap()
    {
        return Collections.unmodifiableMap(packageNameMap);
    }

    private final LinkedHashMap<PackageName, Package> packageNameMap;
}
