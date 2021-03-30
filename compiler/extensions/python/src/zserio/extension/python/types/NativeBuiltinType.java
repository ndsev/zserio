package zserio.extension.python.types;

import zserio.ast.PackageName;

/**
 * Native Python built-in type mapping.
 */
public class NativeBuiltinType extends PythonNativeType
{
    public NativeBuiltinType(String builtinTypeName)
    {
        super(BUILTIN_TYPE_PACKAGE_NAME, BUILTIN_TYPE_MODULE_NAME, builtinTypeName);
    }

    private static final PackageName BUILTIN_TYPE_PACKAGE_NAME = PackageName.EMPTY;
    private static final String BUILTIN_TYPE_MODULE_NAME = "";
}
