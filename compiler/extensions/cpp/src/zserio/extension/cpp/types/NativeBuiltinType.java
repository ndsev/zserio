package zserio.extension.cpp.types;

import zserio.ast.PackageName;

/**
 * Native C++ built-in type mapping.
 */
public class NativeBuiltinType extends NativeType implements CppNativeArrayableType
{
    public NativeBuiltinType(String builtinTypeName, NativeArrayTraits arrayTraits)
    {
        super(PackageName.EMPTY, builtinTypeName, true, arrayTraits.getSystemIncludeFiles(),
                arrayTraits.getUserIncludeFiles());

        this.arrayTraits = arrayTraits;
    }

    @Override
    public NativeArrayTraits getArrayTraits()
    {
        return arrayTraits;
    }

    private NativeArrayTraits arrayTraits;
}
