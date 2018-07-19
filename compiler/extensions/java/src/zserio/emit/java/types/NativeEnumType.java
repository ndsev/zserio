package zserio.emit.java.types;

public class NativeEnumType extends JavaNativeType
{
    public NativeEnumType(String packageName, String name, NativeIntegralType nativeBaseType)
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
