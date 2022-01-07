package zserio.extension.java.types;

import zserio.ast.PackageName;

/**
 * Native Java bitmask type mapping.
 */
public class NativeBitmaskType extends NativeArrayableType
{
    public NativeBitmaskType(PackageName packageName, String name, NativeIntegralType nativeBaseType,
            boolean withWriterCode)
    {
        super(packageName, name,
                new NativeObjectRawArray(),
                new NativeObjectArrayTraits(packageName, name, withWriterCode),
                new NativeObjectArrayElement(packageName, name));

        this.nativeBaseType = nativeBaseType;
    }

    public NativeIntegralType getBaseType()
    {
        return nativeBaseType;
    }

    @Override
    public boolean isSimple()
    {
        return false;
    }

    private final NativeIntegralType nativeBaseType;
}
