package zserio.emit.cpp98;

import zserio.ast.PackageName;
import zserio.tools.StringJoinUtil;

/**
 * The class handles the C++ full name construction.
 */
public final class CppFullNameFormatter
{
    /**
     * Constructs full C++ name from the given package name - i.e. constructs full namespace.
     *
     * @param packageName Package name.
     *
     * @return Full namespace.
     */
    public static String getFullName(PackageName packageName)
    {
        return packageName.toString(CPP_NAMESPACE_SEPARATOR);
    }

    /**
     * Constructs full C++ name from package name and type or symbol name.
     *
     * @param packageName Package name.
     * @param name        Type or symbol name.
     *
     * @return Full name.
     */
    public static String getFullName(PackageName packageName, String name)
    {
        return StringJoinUtil.joinStrings(getFullName(packageName), name,
                CPP_NAMESPACE_SEPARATOR);
    }

    /**
     * Constructs full C++ name from package name, type name and member name.
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
                CPP_NAMESPACE_SEPARATOR);
    }

    private static final String CPP_NAMESPACE_SEPARATOR = "::";
}
