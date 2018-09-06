package zserio.emit.java.types;

public class NativeServiceType extends JavaNativeType
{
    public NativeServiceType(String packageName, String name)
    {
        super(packageName, name);
    }

    @Override
    public boolean isSimple()
    {
        return false;
    }
}
