package zserio.extension.python;

import zserio.ast.PackageName;
import zserio.extension.python.symbols.PythonNativeSymbol;
import zserio.tools.StringJoinUtil;

/**
 * The class handles the Python full name construction.
 */
public final class PythonFullNameFormatter
{
    public static String getFullName(PythonNativeSymbol symbol)
    {
        return StringJoinUtil.joinStrings(getFullName(symbol.getPackageName()),
                symbol.getModuleName(), symbol.getName(), PYTHON_PACKAGE_SEPARATOR);
    }

    public static String getModuleFullName(PackageName packageName, String moduleName)
    {
        return StringJoinUtil.joinStrings(getFullName(packageName), moduleName, PYTHON_PACKAGE_SEPARATOR);
    }

    public static String getModuleFullName(PythonNativeSymbol symbol)
    {
        return getModuleFullName(symbol.getPackageName(), symbol.getModuleName());
    }

    public static String getFullName(PackageName packageName)
    {
        return packageName.toString(PYTHON_PACKAGE_SEPARATOR);
    }

    private static final String PYTHON_PACKAGE_SEPARATOR = ".";
}
