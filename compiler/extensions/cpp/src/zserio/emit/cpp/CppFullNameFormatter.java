package zserio.emit.cpp;

import zserio.tools.StringJoinUtil;

/**
 * The class handles the C++ full name construction.
 */
public final class CppFullNameFormatter
{
    /**
     * Constructs full C++ name from package name and type name.
     *
     * @param namespaceName Namespace name (can be empty).
     * @param typeName      Type name.
     */
    public static String getFullName(String namespaceName, String typeName)
    {
        return StringJoinUtil.joinStrings(namespaceName, typeName, CPP_NAMESPACE_SEPARATOR);
    }

    public static final String CPP_NAMESPACE_SEPARATOR = "::";
}
