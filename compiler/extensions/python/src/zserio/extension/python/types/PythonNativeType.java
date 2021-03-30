package zserio.extension.python.types;

import zserio.ast.PackageName;
import zserio.extension.python.symbols.PythonNativeSymbol;

/**
 * Python native type mapping - e.g. compound type, subtype, etc.
 */
public class PythonNativeType extends PythonNativeSymbol
{
    protected PythonNativeType(PackageName packageName, String moduleName, String name)
    {
        super(packageName, moduleName, name);
    }
}
