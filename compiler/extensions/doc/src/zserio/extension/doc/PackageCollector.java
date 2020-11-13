package zserio.extension.doc;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import zserio.ast.Package;
import zserio.ast.ZserioAstWalker;

class PackageCollector extends ZserioAstWalker
{
    public Map<Path, Package> getPathToPackageMap()
    {
        return packageMap;
    }

    @Override
    public void visitPackage(Package pkg)
    {
        final Path path = Paths.get(pkg.getLocation().getFileName()).toAbsolutePath().normalize();
        packageMap.put(path, pkg);
    }

    private final Map<Path, Package> packageMap = new HashMap<Path, Package>();
}
