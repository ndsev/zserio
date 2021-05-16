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
        super(packageName, PythonSymbolConverter.symbolToModule(name), name,
                new NativeArrayTraits("ObjectArrayTraits", false, true));
    }
}
