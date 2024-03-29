package zserio.extension.python.types;

import zserio.ast.PackageName;

/**
 * Native Python built-in type mapping.
 */
public final class NativeBuiltinType extends PythonNativeType
{
    public NativeBuiltinType(String builtinTypeName, NativeArrayTraits arrayTraits)
    {
        super(BUILTIN_TYPE_PACKAGE_NAME, BUILTIN_TYPE_MODULE_NAME, builtinTypeName, arrayTraits);
    }

    private static final PackageName BUILTIN_TYPE_PACKAGE_NAME = PackageName.EMPTY;
    private static final String BUILTIN_TYPE_MODULE_NAME = "";
}
