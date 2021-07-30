package zserio.extension.java.types;

import zserio.ast.PackageName;

public abstract class NativeArrayableType extends JavaNativeType
{
    public NativeArrayableType(PackageName packageName, String name, NativeArrayTraits arrayTraits)
    {
        super(packageName, name);

        this.arrayTraits = arrayTraits;
    }

    public NativeArrayTraits getArrayTraits()
    {
        return arrayTraits;
    }

    private final NativeArrayTraits arrayTraits;
}
