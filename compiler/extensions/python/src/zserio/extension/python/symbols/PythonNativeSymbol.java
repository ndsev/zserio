package zserio.extension.python.symbols;

import zserio.ast.PackageName;
import zserio.extension.python.PythonFullNameFormatter;
import zserio.extension.python.PythonSymbolConverter;

/**
 * Python native symbol - e.g. constant, compound type, subtype, etc.
 */
public class PythonNativeSymbol
{
    /**
     * Constructor.
     *
     * @param packageName     Package name where the symbol is located.
     * @param name            Name of the symbol.
     */
    public PythonNativeSymbol(PackageName packageName, String name)
    {
        this.packageName = packageName;
        this.name = PythonSymbolConverter.constantToSymbol(name);
    };

    /**
     * Gets name of the package where the native symbol is located.
     *
     * @return Package name.
     */
    public PackageName getPackageName()
    {
        return packageName;
    }

    /**
     * Gets symbol name.
     *
     * @return Symbol name.
     */
    public String getName()
    {
        return name;
    }

    /**
     * Gets full name of the symbol.
     *
     * @return Full symbol name.
     */
    public String getFullName()
    {
        return PythonFullNameFormatter.getFullName(packageName, name);
    }

    private final PackageName packageName;
    private final String name;
};
