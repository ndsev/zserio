package zserio.emit.common;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import zserio.ast.ZserioType;
import zserio.ast.Package;
import zserio.tools.PackageManager;
import zserio.tools.StringJoinUtil;

/**
 * The class maps the Zserio package name into language dependent package name.
 *
 * Note that Zserio package names cannot be used directly because user can defined custom top level package
 * using command line argument.
 */
final public class PackageMapper
{
    /**
     * Constructor.
     *
     * @param topLevelPackageNameList The list of top level package names defined in the command line.
     * @param codePackageSeparator    The language package separator to use.
     */
    public PackageMapper(Iterable<String> topLevelPackageNameList, String codePackageSeparator)
    {
        this.codePackageSeparator = codePackageSeparator;

        final List<String> topLevelPath = new ArrayList<String>();
        for (String component: topLevelPackageNameList)
            topLevelPath.add(component);
        topLevelPackageNamePath = Collections.unmodifiableList(topLevelPath);

        final Package rootPackage = PackageManager.get().getRoot();
        mappedRootPackagePath = Collections.unmodifiableList(getMappedPackagePath(rootPackage));
        mappedRootPackageName = getMappedPackageName(rootPackage);
        mappedRootPackageFilePath = getMappedPackageFilePath(rootPackage);
    }

    /**
     * Returns the mapped root package name.
     */
    public String getRootPackageName()
    {
        return mappedRootPackageName;
    }

    /**
     * Returns the relative path to mapped root directory.
     */
    public String getRootPackageFilePath()
    {
        return mappedRootPackageFilePath;
    }

    /**
     * Returns the components of the root package.
     */
    public List<String> getRootPackagePath()
    {
        return mappedRootPackagePath;
    }

    /**
     * Returns the mapped package name of the given zserio type.
     *
     * @param type The zserio type to use.
     */
    public String getPackageName(ZserioType type)
    {
        return getMappedPackageName(type.getPackage());
    }

    /**
     * Returns the relative path to mapped package of the given zserio type.
     *
     * @param type The zserio type to use.
     */
    public String getPackageFilePath(ZserioType type)
    {
        return getMappedPackageFilePath(type.getPackage());
    }

    /**
     * Returns the package a zserio type belongs in as a list of components.
     * @param type
     * @return The package as a list of its components.
     */
    public List<String> getPackagePath(ZserioType type)
    {
        return getMappedPackagePath(type.getPackage());
    }

    private List<String> getMappedPackagePath(Package zserioPackage)
    {
        final List<String> packagePath = zserioPackage.getPackagePath();
        final List<String> path = new ArrayList<String>(topLevelPackageNamePath.size() + packagePath.size());
        path.addAll(topLevelPackageNamePath);
        path.addAll(packagePath);
        return path;
    }

    private String getMappedPackageName(Package zserioPackage)
    {
        return StringJoinUtil.joinStrings(topLevelPackageNamePath, zserioPackage.getPackagePath(),
                codePackageSeparator);
    }

    private String getMappedPackageFilePath(Package zserioPackage)
    {
        return StringJoinUtil.joinStrings(topLevelPackageNamePath, zserioPackage.getPackagePath(),
                File.separator);
    }

    private final List<String>      topLevelPackageNamePath;
    private final List<String>      mappedRootPackagePath;

    private final String            codePackageSeparator;
    private final String            mappedRootPackageName;
    private final String            mappedRootPackageFilePath;
}
