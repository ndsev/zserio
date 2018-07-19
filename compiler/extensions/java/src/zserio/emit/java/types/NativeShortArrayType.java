package zserio.emit.java.types;

public class NativeShortArrayType extends NativeArrayType
{
    public NativeShortArrayType(JavaNativeType elementType)
    {
        super("ShortArray", elementType);
    }

    @Override
    public boolean requiresElementBitSize()
    {
        return true;
    }
}
