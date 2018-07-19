package zserio.emit.java.types;

public class NativeLongArrayType extends NativeArrayType
{
    public NativeLongArrayType(JavaNativeType elementType)
    {
        super("LongArray", elementType);
    }

    @Override
    public boolean requiresElementBitSize()
    {
        return true;
    }
}
