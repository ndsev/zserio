package zserio.extension.java.symbols;

import zserio.ast.PackageName;
import zserio.extension.java.JavaFullNameFormatter;

/**
 * Java native symbol - e.g. constant.
 */
public class JavaNativeSymbol
{
    public JavaNativeSymbol(PackageName packageName, String name)
    {
        this.packageName = packageName;
        this.name = name;
    };

    public PackageName getPackageName()
    {
        return packageName;
    }

    public String getName()
    {
        return name;
    }

    public String getFullName()
    {
        return JavaFullNameFormatter.getFullName(packageName, name, name);
    }

    private final PackageName packageName;
    private final String name;
};
