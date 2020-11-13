package zserio.extension.java.types;

public class NativeIntArrayType extends NativeArrayType
{
    public NativeIntArrayType()
    {
        super("IntArray");
    }

    @Override
    public boolean requiresElementBitSize()
    {
        return true;
    }
}
