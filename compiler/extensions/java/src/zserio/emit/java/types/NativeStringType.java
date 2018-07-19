package zserio.emit.java.types;

public class NativeStringType extends JavaNativeType
{
    public NativeStringType()
    {
        super("", "String");
    }

    @Override
    public boolean isSimple()
    {
        return false;
    }
}
