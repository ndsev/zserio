package zserio.emit.java.types;

public class NativeUnsignedIntArrayType extends NativeArrayType
{
    public NativeUnsignedIntArrayType(JavaNativeType elementType)
    {
        super("UnsignedIntArray", elementType);
    }

    @Override
    public boolean requiresElementBitSize()
    {
        return true;
    }
}
