package zserio.emit.java.types;

public class NativeIntArrayType extends NativeArrayType
{
    public NativeIntArrayType(JavaNativeType elementType)
    {
        super("IntArray", elementType);
    }

    @Override
    public boolean requiresElementBitSize()
    {
        return true;
    }
}
