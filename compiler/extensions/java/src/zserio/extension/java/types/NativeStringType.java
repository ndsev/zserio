package zserio.extension.java.types;

public class NativeStringType extends JavaNativeType
{
    public NativeStringType()
    {
        super(JAVA_LANG_PACKAGE, "String");
    }

    @Override
    public boolean isSimple()
    {
        return false;
    }
}
