package zserio.emit.java.types;

public class NativeByteArrayType extends NativeArrayType
{
    public NativeByteArrayType()
    {
        super("ByteArray");
    }

    @Override
    public boolean requiresElementBitSize()
    {
        return true;
    }
}
