package zserio.emit.java.types;

public class NativeUnsignedLongArrayType extends NativeArrayType
{
    public NativeUnsignedLongArrayType(JavaNativeType elementType)
    {
        super("UnsignedLongArray", elementType);
    }

    @Override
    public boolean requiresElementBitSize()
    {
        return true;
    }
}
