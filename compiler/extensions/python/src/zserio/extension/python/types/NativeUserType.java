package zserio.extension.python.types;

import zserio.ast.PackageName;
import zserio.extension.python.PythonSymbolConverter;

/**
 * Python native user type mapping - e.g. compound type, SQL database, etc.
 */
public class NativeUserType extends PythonNativeType
{
    public NativeUserType(PackageName packageName, String name)
    {
        this(packageName, name, new NativeArrayTraits("ObjectArrayTraits", false, true));
    }

    protected NativeUserType(PackageName packageName, String name, NativeArrayTraits arrayTraits)
    {
        super(packageName, PythonSymbolConverter.symbolToModule(name), name, arrayTraits);
    }
}
