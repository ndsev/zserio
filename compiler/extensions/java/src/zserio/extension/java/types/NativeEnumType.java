package zserio.extension.java.types;

import zserio.ast.PackageName;

/**
 * Native Java enumeration type mapping.
 */
public final class NativeEnumType extends NativeArrayableType
{
    public NativeEnumType(PackageName packageName, String name, NativeIntegralType nativeBaseType,
            boolean withWriterCode)
    {
        super(packageName, name,
                new NativeEnumRawArray(),
                new NativeObjectArrayTraits(packageName, name, withWriterCode, true),
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
