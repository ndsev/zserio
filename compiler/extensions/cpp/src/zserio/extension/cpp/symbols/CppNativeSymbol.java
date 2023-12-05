package zserio.extension.cpp.symbols;

import zserio.ast.PackageName;
import zserio.extension.cpp.CppFullNameFormatter;

/**
 * C++ native symbol - e.g. constant.
 */
public final class CppNativeSymbol
{
    public CppNativeSymbol(PackageName packageName, String name, String includeFileName)
    {
        this.packageName = packageName;
        this.name = name;
        this.includeFileName = includeFileName;
    };

    public PackageName getPackageName()
    {
        return packageName;
    }

    public String getName()
    {
        return name;
    }

    public String getIncludeFile()
    {
        return includeFileName;
    }

    public String getFullName()
    {
        return CppFullNameFormatter.getFullName(packageName, name);
    }

    private final PackageName packageName;
    private final String name;
    private final String includeFileName;
};
