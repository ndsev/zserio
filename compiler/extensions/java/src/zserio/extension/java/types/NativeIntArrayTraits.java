package zserio.extension.java.types;

public class NativeIntArrayTraits extends NativeArrayTraits
{
    public NativeIntArrayTraits(String name)
    {
        super(name);
    }

    @Override
    public boolean requiresElementBitSize()
    {
        return true;
    }
}
