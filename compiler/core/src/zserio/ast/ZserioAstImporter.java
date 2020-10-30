package zserio.ast;

import java.util.Map;

/**
 * Implementation of ZserioAstVisitor which manages type importing phase.
 */
public class ZserioAstImporter extends ZserioAstWalker
{
    @Override
    public void visitRoot(Root root)
    {
        packageNameMap = root.getPackageNameMap();

        root.visitChildren(this);

        packageNameMap = null;
    }

    @Override
    public void visitPackage(Package unitPackage)
    {
        unitPackage.visitChildren(this);
        unitPackage.resolveImports();
    }

    @Override
    public void visitImport(Import unitImport)
    {
        unitImport.resolveImport(packageNameMap);
    }

    private Map<PackageName, Package> packageNameMap = null;
}