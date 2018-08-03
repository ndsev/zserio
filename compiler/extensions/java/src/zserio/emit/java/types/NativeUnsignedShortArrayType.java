package zserio.emit.java.types;

public class NativeUnsignedShortArrayType extends NativeArrayType
{
    public NativeUnsignedShortArrayType()
    {
        super("UnsignedShortArray");
    }

    @Override
    public boolean requiresElementBitSize()
    {
        return true;
    }
}
