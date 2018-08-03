package zserio.emit.java.types;

public class NativeUnsignedIntArrayType extends NativeArrayType
{
    public NativeUnsignedIntArrayType()
    {
        super("UnsignedIntArray");
    }

    @Override
    public boolean requiresElementBitSize()
    {
        return true;
    }
}
