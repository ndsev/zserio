package zserio.emit.java.types;

public class NativeByteArrayType extends NativeArrayType
{
    public NativeByteArrayType(JavaNativeType elementType)
    {
        super("ByteArray", elementType);
    }

    @Override
    public boolean requiresElementBitSize()
    {
        return true;
    }
}
