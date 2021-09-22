package zserio.ast;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import zserio.extension.common.TreeWalker;
import zserio.extension.common.ZserioExtensionException;

/**
 * The representation of AST ROOT node.
 *
 * This class represents an AST node which has all translation units compiled by Zserio.
 */
public class Root extends AstNodeBase
{
    /**
     * Constructor.
     *
     * @param packageNameMap Map of all available packages.
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
     * Gets the root package.
     *
     * Root package is the package which is specified in the source file on the command line. It always exists.
     *
     * @return Root package.
     */
    public Package getRootPackage()
    {
        return packageNameMap.values().iterator().next();
    }

    /**
     * Walks through AST tree applying given TreeWalker interface.
     *
     * @param walker TreeWalker interface to use for walking.
     *
     * @throws ZserioExtensionException Throws in case of any error.
     */
    public void walk(TreeWalker walker) throws ZserioExtensionException
    {
        final ZserioAstTreeWalker walkingVisitor = new ZserioAstTreeWalker(walker);
        try
        {
            this.accept(walkingVisitor);
        }
        catch (ZserioAstTreeWalker.UncheckedZserioExtensionException e)
        {
            throw e.getOriginalException();
        }
    }

    /**
     * Checks the root node.
     */
    void check()
    {
        final RuleIdUniqueChecker checker = new RuleIdUniqueChecker();
        accept(checker);
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
