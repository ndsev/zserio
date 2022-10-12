package zserio.extension.java;

import zserio.ast.PackageName;
import zserio.tools.StringJoinUtil;

/**
 * The class handles the Java full name construction.
 */
final public class JavaFullNameFormatter
{
    public static String getFullName(PackageName packageName)
    {
        return packageName.toString(JAVA_PACKAGE_SEPARATOR);
    }

    public static String getFullName(PackageName packageName, String name)
    {
        return StringJoinUtil.joinStrings(getFullName(packageName), name,
                JAVA_PACKAGE_SEPARATOR);
    }

    public static String getFullName(PackageName packageName, String typeName, String memberName)
    {
        return StringJoinUtil.joinStrings(getFullName(packageName, typeName), memberName,
                JAVA_PACKAGE_SEPARATOR);
    }

    static final String JAVA_PACKAGE_SEPARATOR = ".";
}
