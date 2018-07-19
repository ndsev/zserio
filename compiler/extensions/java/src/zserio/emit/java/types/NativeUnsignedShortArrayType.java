package zserio.emit.java.types;

public class NativeUnsignedShortArrayType extends NativeArrayType
{
    public NativeUnsignedShortArrayType(JavaNativeType elementType)
    {
        super("UnsignedShortArray", elementType);
    }

    @Override
    public boolean requiresElementBitSize()
    {
        return true;
    }
}
