package zserio.emit.java.symbols;

import zserio.ast.PackageName;
import zserio.emit.java.JavaFullNameFormatter;

/**
 * C++ native symbol - e.g. constant.
 */
public class JavaNativeSymbol
{
    /**
     * Constructor.
     *
     * @param packageName     Package name where the symbol is located.
     * @param name            Name of the symbol.
     */
    public JavaNativeSymbol(PackageName packageName, String name)
    {
        this.packageName = packageName;
        this.name = name;
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
        return JavaFullNameFormatter.getFullName(packageName, name, name);
    }

    private final PackageName packageName;
    private final String name;
};
