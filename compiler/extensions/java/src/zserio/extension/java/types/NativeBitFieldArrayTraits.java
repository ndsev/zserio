package zserio.extension.java.types;

public class NativeBitFieldArrayTraits extends NativeArrayTraits
{
    public NativeBitFieldArrayTraits(String name)
    {
        super(name);
    }

    @Override
    public boolean requiresElementBitSize()
    {
        return true;
    }
}
