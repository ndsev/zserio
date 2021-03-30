package zserio.extension.python.symbols;

import zserio.ast.PackageName;

/**
 * Python native symbol mapping - e.g. constant, compound type, subtype, etc.
 */
public class PythonNativeSymbol
{
    public PythonNativeSymbol(PackageName packageName, String moduleName, String name)
    {
        this.packageName = packageName;
        this.moduleName = moduleName;
        this.name = name;
    }

    public PackageName getPackageName()
    {
        return packageName;
    }

    public String getModuleName()
    {
        return moduleName;
    }

    public String getName()
    {
        return name;
    }

    private final PackageName packageName;
    private final String moduleName;
    private final String name;
};
