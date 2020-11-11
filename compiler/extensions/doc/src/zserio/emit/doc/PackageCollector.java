package zserio.emit.doc;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import zserio.ast.Package;
import zserio.ast.ZserioAstWalker;
import zserio.tools.Parameters;

class PackageCollector extends ZserioAstWalker
{
    public PackageCollector(Parameters extensionParameters)
    {
        final String pathName = extensionParameters.getPathName();
        this.sourceRoot = Paths.get(pathName != null ? pathName : "").toAbsolutePath();
    }

    public Map<Path, Package> getPathToPackageMap()
    {
        return packageMap;
    }

    @Override
    public void visitPackage(Package pkg)
    {
        final Path path = sourceRoot.resolve(pkg.getLocation().getFileName()).normalize();

        packageMap.put(path, pkg);
    }

    private final Path sourceRoot;
    private final Map<Path, Package> packageMap = new HashMap<Path, Package>();
}
