package zserio.extension.java.types;

import zserio.ast.PackageName;

public class NativeEnumType extends NativeArrayableType
{
    public NativeEnumType(PackageName packageName, String name, NativeIntegralType nativeBaseType,
            boolean withWriterCode)
    {
        super(packageName, name, new NativeObjectArrayTraits(packageName, name, withWriterCode));

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
