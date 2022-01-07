package zserio.extension.java.types;

import zserio.ast.PackageName;

/**
 * Native Java mapping for types which can be element of an array.
 */
public abstract class NativeArrayableType extends JavaNativeType
{
    public NativeArrayableType(PackageName packageName, String name,
            NativeRawArray rawArray, NativeArrayTraits arrayTraits, NativeArrayElement arrayElement)
    {
        super(packageName, name);

        this.rawArray = rawArray;
        this.arrayTraits = arrayTraits;
        this.arrayElement= arrayElement;
    }

    public NativeRawArray getRawArray()
    {
        return rawArray;
    }

    public NativeArrayTraits getArrayTraits()
    {
        return arrayTraits;
    }

    public NativeArrayElement getArrayElement()
    {
        return arrayElement;
    }

    private final NativeRawArray rawArray;
    private final NativeArrayTraits arrayTraits;
    private final NativeArrayElement arrayElement;
}
