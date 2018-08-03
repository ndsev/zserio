package zserio.emit.java.types;

public class NativeLongArrayType extends NativeArrayType
{
    public NativeLongArrayType()
    {
        super("LongArray");
    }

    @Override
    public boolean requiresElementBitSize()
    {
        return true;
    }
}
