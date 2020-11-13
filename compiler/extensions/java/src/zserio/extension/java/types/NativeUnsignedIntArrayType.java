package zserio.extension.java.types;

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
