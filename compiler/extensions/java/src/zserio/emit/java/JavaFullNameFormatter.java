package zserio.emit.java;

import zserio.tools.StringJoinUtil;

/**
 * The class handles the Java full name construction.
 */
final public class JavaFullNameFormatter
{
    /**
     * Constructs full Java name from package name and type name.
     *
     * @param packageName Package name. Can be empty.
     * @param typeName    Type name.
     */
    public static String getFullName(String packageName, String typeName)
    {
        return StringJoinUtil.joinStrings(packageName, typeName, JAVA_PACKAGE_SEPARATOR);
    }

    public static final String JAVA_PACKAGE_SEPARATOR = ".";
}
