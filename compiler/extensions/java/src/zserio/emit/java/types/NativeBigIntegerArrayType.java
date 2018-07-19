package zserio.emit.java.types;

public class NativeBigIntegerArrayType extends NativeArrayType
{
    public NativeBigIntegerArrayType(JavaNativeType elementType)
    {
        super("BigIntegerArray", elementType);
    }

    @Override
    public boolean requiresElementBitSize()
    {
        return true;
    }
}
