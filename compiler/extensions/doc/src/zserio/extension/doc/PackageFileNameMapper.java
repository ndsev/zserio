package zserio.extension.doc;

import zserio.ast.Package;
import zserio.ast.PackageName;

class PackageFileNameMapper
{
    public static String getFileName(Package pkg)
    {
        final PackageName packageName = pkg.getPackageName();
        final String packageFileName = (packageName.isEmpty()) ? DEFAULT_PACKAGE_FILE_NAME :
            packageName.toString();

        return packageFileName;
    }

    // Default package file name. It cannot clash because 'zserio' is reserved id.
    private static String DEFAULT_PACKAGE_FILE_NAME = "zserio_default";
}
