package zserio.emit.cpp.symbols;

import zserio.ast.PackageName;
import zserio.emit.cpp.CppFullNameFormatter;

/**
 * C++ native symbol - e.g. constant.
 */
public class CppNativeSymbol
{
    /**
     * Constructor.
     *
     * @param packageName     Package name where the symbol is located.
     * @param name            Name of the symbol.
     * @param includeFileName Filename for include of the symbol definition.
     */
    public CppNativeSymbol(PackageName packageName, String name, String includeFileName)
    {
        this.packageName = packageName;
        this.name = name;
        this.includeFileName = includeFileName;
    };

    /**
     * Gets name of the package where the symbol is located.
     *
     * @return Package name.
     */
    public PackageName getPackageName()
    {
        return packageName;
    }

    /**
     * Gets name of the symbol.
     *
     * @return Symbol name.
     */
    public String getName()
    {
        return name;
    }

    /**
     * Gets filename of the include file with symbol definition.
     *
     * @return Include filename.
     */
    public String getIncludeFile()
    {
        return includeFileName;
    }

    /**
     * Gets symbol's full name.
     *
     * @return Full name.
     */
    public String getFullName()
    {
        return CppFullNameFormatter.getFullName(packageName, name);
    }

    private final PackageName packageName;
    private final String name;
    private final String includeFileName;
};
