package zserio.extension.java.types;

public class NativeUnsignedByteArrayType extends NativeArrayType
{
    public NativeUnsignedByteArrayType()
    {
        super("UnsignedByteArray");
    }

    @Override
    public boolean requiresElementBitSize()
    {
        return true;
    }
}
