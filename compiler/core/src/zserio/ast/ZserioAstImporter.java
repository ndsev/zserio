package zserio.ast;

import java.util.Map;

import zserio.tools.WarningsConfig;

/**
 * Implementation of ZserioAstVisitor which manages type importing phase.
 */
public class ZserioAstImporter extends ZserioAstWalker
{
    /**
     * Constructor.
     *
     * @param warningsConfig Warnings subsystem configuration.
     */
    public ZserioAstImporter(WarningsConfig warningsConfig)
    {
        this.warningsConfig = warningsConfig;
    }

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
        unitPackage.resolveImports(warningsConfig);
    }

    @Override
    public void visitImport(Import unitImport)
    {
        unitImport.resolveImport(packageNameMap);
    }

    private final WarningsConfig warningsConfig;
    private Map<PackageName, Package> packageNameMap = null;
}
