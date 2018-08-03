package zserio.emit.java.types;

public class NativeUnsignedLongArrayType extends NativeArrayType
{
    public NativeUnsignedLongArrayType()
    {
        super("UnsignedLongArray");
    }

    @Override
    public boolean requiresElementBitSize()
    {
        return true;
    }
}
