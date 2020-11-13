package zserio.extension.java;

import zserio.ast.PackageName;
import zserio.tools.StringJoinUtil;

/**
 * The class handles the Java full name construction.
 */
final public class JavaFullNameFormatter
{
    /**
     * Constructs full Java name from the given package name.
     *
     * @param packageName Package name.
     *
     * @return Full package name.
     */
    public static String getFullName(PackageName packageName)
    {
        return packageName.toString(JAVA_PACKAGE_SEPARATOR);
    }

    /**
     * Constructs full Java name from package name and type or symbol name.
     *
     * @param packageName Package name.
     * @param name        Type or symbol name.
     *
     * @return Full name.
     */
    public static String getFullName(PackageName packageName, String name)
    {
        return StringJoinUtil.joinStrings(getFullName(packageName), name,
                JAVA_PACKAGE_SEPARATOR);
    }

    /**
     * Constructs full Java name from package name, type name and member name.
     *
     * @param packageName Package name.
     * @param typeName    Type name.
     * @param memberName  Member name.
     *
     * @return Full name of a member function or a static member variable.
     */
    public static String getFullName(PackageName packageName, String typeName, String memberName)
    {
        return StringJoinUtil.joinStrings(getFullName(packageName, typeName), memberName,
                JAVA_PACKAGE_SEPARATOR);
    }

    private static final String JAVA_PACKAGE_SEPARATOR = ".";
}
