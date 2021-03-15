package zserio.extension.python;

import zserio.ast.PackageName;
import zserio.tools.StringJoinUtil;

/**
 * The class handles the Python full name construction.
 */
public class PythonFullNameFormatter
{
    public static String getFullName(PackageName packageName)
    {
        return packageName.toString(PYTHON_PACKAGE_SEPARATOR);
    }

    public static String getFullName(PackageName packageName, String packageSymbolName)
    {
        final String moduleName = PythonSymbolConverter.symbolToModule(packageSymbolName);

        return getFullName(packageName, moduleName, packageSymbolName);
    }

    public static String getFullName(PackageName packageName, String moduleName, String name)
    {
        return StringJoinUtil.joinStrings(getFullName(packageName), moduleName, name,
                PYTHON_PACKAGE_SEPARATOR);
    }

    public static String getFullModuleImportName(PackageName packageName, String symbolName)
    {
        final String moduleName = PythonSymbolConverter.symbolToModule(symbolName);

        return StringJoinUtil.joinStrings(getFullName(packageName), moduleName, PYTHON_PACKAGE_SEPARATOR);
    }

    private static final String PYTHON_PACKAGE_SEPARATOR = ".";
}
