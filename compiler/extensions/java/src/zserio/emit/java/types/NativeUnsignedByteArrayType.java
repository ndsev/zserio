package zserio.emit.java.types;

public class NativeUnsignedByteArrayType extends NativeArrayType
{
    public NativeUnsignedByteArrayType(JavaNativeType elementType)
    {
        super("UnsignedByteArray", elementType);
    }

    @Override
    public boolean requiresElementBitSize()
    {
        return true;
    }
}
