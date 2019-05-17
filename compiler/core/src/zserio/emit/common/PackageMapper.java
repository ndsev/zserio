package zserio.emit.common;

import zserio.ast.Package;
import zserio.ast.PackageName;
import zserio.ast.ZserioType;

/**
 * The class maps the Zserio package name into language dependent package name.
 *
 * Note that Zserio package names cannot be used directly because user can defined custom top level package
 * using command line argument.
 */
public final class PackageMapper
{
    /**
     * Constructor.
     *
     * @param topLevelPackageNameList The list of top level package names defined in the command line.
     */
    public PackageMapper(Package rootPackage, Iterable<String> topLevelPackageNameList)
    {
        topLevelPackageName = new PackageName.Builder().addIds(topLevelPackageNameList).get();
        mappedRootPackageName = getPackageName(rootPackage);
    }

    /**
     * Gets the mapped root package name.
     *
     * @return Mapped root package name.
     */
    public PackageName getRootPackageName()
    {
        return mappedRootPackageName;
    }

    /**
     * Returns the mapped package name for the given zserio type.
     * Provided for convenience.
     *
     * @param type The zserio type to use.
     *
     * @return Mapped package name.
     */
    public PackageName getPackageName(ZserioType type)
    {
        return getMappedPackageName(type.getPackage().getPackageName());
    }

    /**
     * Returns the mapped package name for the given zserio package.
     * Provided for convenience.
     *
     * @param zserioPackage The zserio package to use.
     *
     * @return Mapped package name.
     */
    public PackageName getPackageName(Package zserioPackage)
    {
        return getMappedPackageName(zserioPackage.getPackageName());
    }

    /**
     * Maps the given package name.
     *
     * @param packageName Package name to map.
     *
     * @return Mapped package name.
     */
    public PackageName getPackageName(PackageName packageName)
    {
        return getMappedPackageName(packageName);
    }

    private PackageName getMappedPackageName(PackageName packageName)
    {
        final PackageName.Builder mappedPackageNameBuilder = new PackageName.Builder();
        mappedPackageNameBuilder.append(topLevelPackageName);
        mappedPackageNameBuilder.append(packageName);
        return mappedPackageNameBuilder.get();
    }

    private final PackageName topLevelPackageName;
    private final PackageName mappedRootPackageName;
}
