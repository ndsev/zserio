package zserio.extension.java.types;

import zserio.ast.PackageName;

/**
 * Native Java bytes type mapping.
 */
public final class NativeBytesType extends NativeArrayableType
{
    public NativeBytesType()
    {
        super(PackageName.EMPTY, "byte[]", new NativeRawArray("BytesRawArray"),
                new NativeArrayTraits("BytesArrayTraits"),
                new NativeObjectArrayElement(PackageName.EMPTY, "byte[]"));
    }

    @Override
    public boolean isSimple()
    {
        return false;
    }
}
