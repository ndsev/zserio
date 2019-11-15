package zserio.emit.python;

import zserio.ast.PackageName;
import zserio.tools.StringJoinUtil;

/**
 * The class handles the Python full name construction.
 */
final public class PythonFullNameFormatter
{
    /**
     * Constructs full Python name from the given package name.
     *
     * @param packageName Package name.
     *
     * @return Full package name.
     */
    public static String getFullName(PackageName packageName)
    {
        return packageName.toString(PYTHON_PACKAGE_SEPARATOR);
    }

    /**
     * Constructs full Python name from package name and type or symbol name.
     *
     * @param packageName Package name.
     * @param name        Type or symbol name.
     *
     * @return Full name.
     */
    public static String getFullName(PackageName packageName, String name)
    {
        final String moduleName = name;
        return StringJoinUtil.joinStrings(getFullName(packageName), moduleName, name,
                PYTHON_PACKAGE_SEPARATOR);
    }

    /**
     * Constructs full Python name from package name, type name and member name.
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
                PYTHON_PACKAGE_SEPARATOR);
    }

    private static final String PYTHON_PACKAGE_SEPARATOR = ".";
}
