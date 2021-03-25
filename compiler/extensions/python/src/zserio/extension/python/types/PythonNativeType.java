package zserio.extension.python.types;

import zserio.ast.PackageName;
import zserio.extension.python.PythonFullNameFormatter;
import zserio.extension.python.symbols.PythonNativeSymbol;

/**
 * Python native symbol - e.g. compound type, subtype, etc.
 */
public class PythonNativeType extends PythonNativeSymbol
{
    protected PythonNativeType(String name)
    {
        super(PackageName.EMPTY, name);
        fullName = name;
    }

    protected PythonNativeType(PackageName packageName, String name)
    {
        super(packageName, name);
        fullName = PythonFullNameFormatter.getFullName(packageName, name);
    }

    protected PythonNativeType(PackageName packageName, String moduleName, String name)
    {
        super(packageName, name);
        fullName = PythonFullNameFormatter.getFullName(packageName, moduleName, name);
    }

    @Override
    public String getFullName()
    {
        return fullName;
    }

    private final String fullName;
}
