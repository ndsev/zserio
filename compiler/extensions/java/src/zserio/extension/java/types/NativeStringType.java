package zserio.extension.java.types;

public class NativeStringType extends NativeArrayableType
{
    public NativeStringType()
    {
        super(JAVA_LANG_PACKAGE, "String", new NativeArrayTraits("StringArray"));
    }

    @Override
    public boolean isSimple()
    {
        return false;
    }
}
