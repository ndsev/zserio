package zserio.ast;

import java.util.Map;

/**
 * Implementation of ZserioAstVisitor which manages type importing phase.
 */
public class ZserioAstTypeImporter extends ZserioAstWalker
{
    @Override
    public void visitRoot(Root root)
    {
        packageNameMap = root.getPackageNameMap();

        root.visitChildren(this);

        packageNameMap = null;
    }

    @Override
    public void visitPackage(Package pkg)
    {
        pkg.importTypes(packageNameMap);
    }

    private Map<PackageName, Package> packageNameMap = null;
}