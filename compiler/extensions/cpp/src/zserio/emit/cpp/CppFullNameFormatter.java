package zserio.emit.cpp;

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
        // TODO: Is it ok to have also ::std::vector / ::std::string? It shall work but seems strange...
        return (packageName.isEmpty() ? "" : CPP_NAMESPACE_SEPARATOR) +
                packageName.toString(CPP_NAMESPACE_SEPARATOR);
    }

    /**
     * Constructs full C++ name from package name and type name.
     *
     * @param packageName Package name.
     * @param typeName    Type name.
     *
     * @return Full type name.
     */
    public static String getFullName(PackageName packageName, String typeName)
    {
        return StringJoinUtil.joinStrings(getFullName(packageName), typeName, CPP_NAMESPACE_SEPARATOR);
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
