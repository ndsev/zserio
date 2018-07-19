package zserio.emit.java.types;

public class NativeCompoundType extends JavaNativeType
{
    public NativeCompoundType(String packageName, String name)
    {
        super(packageName, name);
    }

    @Override
    public boolean isSimple()
    {
        return false;
    }
}
