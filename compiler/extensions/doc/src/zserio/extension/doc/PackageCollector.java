package zserio.extension.doc;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import zserio.ast.Package;
import zserio.ast.ZserioAstWalker;

/**
 * Package collector.
 *
 * Package collector gathers all available packages and builds a special map which maps full source file name
 * path to the package.
 *
 * This map is used by Documentation resource manager to find out referenced package in Markdown documentation
 * comment (e.g. [my schema](zserio_source.zs)).
 */
final class PackageCollector extends ZserioAstWalker
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
