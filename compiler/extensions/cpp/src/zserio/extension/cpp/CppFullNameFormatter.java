package zserio.extension.cpp;

import zserio.ast.PackageName;
import zserio.tools.StringJoinUtil;

/**
 * The class handles the C++ full name construction.
 */
public final class CppFullNameFormatter
{
    public static String getFullName(PackageName packageName)
    {
        return (packageName.isEmpty() ? "" : CPP_NAMESPACE_SEPARATOR) +
                packageName.toString(CPP_NAMESPACE_SEPARATOR);
    }

    public static String getFullName(PackageName packageName, String name)
    {
        return StringJoinUtil.joinStrings(getFullName(packageName), name, CPP_NAMESPACE_SEPARATOR);
    }

    public static String getFullName(PackageName packageName, String typeName, String memberName)
    {
        return StringJoinUtil.joinStrings(getFullName(packageName, typeName), memberName,
                CPP_NAMESPACE_SEPARATOR);
    }

    private static final String CPP_NAMESPACE_SEPARATOR = "::";
}
