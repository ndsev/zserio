package zserio.emit.java.types;

public class NativeArrayType extends JavaNativeType
{
    public NativeArrayType(String name)
    {
        super(RUNTIME_ARRAY_PACKAGE, name);
    }

    @Override
    public boolean isSimple()
    {
        return false;
    }

    public boolean requiresElementBitSize()
    {
        return false;
    }

    public boolean requiresElementFactory()
    {
        return false;
    }

    private final static String RUNTIME_ARRAY_PACKAGE = "zserio.runtime.array";
}
