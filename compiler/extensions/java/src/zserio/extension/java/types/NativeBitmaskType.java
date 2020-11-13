package zserio.extension.java.types;

import zserio.ast.PackageName;

public class NativeBitmaskType extends JavaNativeType
{
    public NativeBitmaskType(PackageName packageName, String name, NativeIntegralType nativeBaseType)
    {
        super(packageName, name);
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
