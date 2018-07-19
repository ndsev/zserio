package zserio.emit.java.types;

public class NativeArrayType extends JavaNativeType
{
    public NativeArrayType(String name, JavaNativeType elementType)
    {
        super(RUNTIME_ARRAY_PACKAGE, name);
        this.elementType = elementType;
    }

    public JavaNativeType getElementType()
    {
        return elementType;
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

    private final JavaNativeType elementType;

    private final static String RUNTIME_ARRAY_PACKAGE = "zserio.runtime.array";
}
