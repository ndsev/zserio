package zserio.extension.cpp.types;

import zserio.ast.PackageName;

/**
 * Native type which can be element of an array.
 */
public abstract class NativeArrayableType extends CppNativeType
{
    public NativeArrayableType(PackageName packageName, String name, NativeArrayTraits arrayTraits)
    {
        super(packageName, name);

        this.arrayTraits = arrayTraits;
    }

    public NativeArrayableType(PackageName packageName, String name, boolean isSimpleType,
            NativeArrayTraits arrayTraits)
    {
        super(packageName, name, isSimpleType);

        this.arrayTraits = arrayTraits;
    }

    public NativeArrayTraits getArrayTraits()
    {
        return arrayTraits;
    }

    private final NativeArrayTraits arrayTraits;
}