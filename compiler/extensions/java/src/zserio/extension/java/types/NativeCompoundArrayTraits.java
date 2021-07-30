package zserio.extension.java.types;

public class NativeCompoundArrayTraits extends NativeArrayTraits
{
    public NativeCompoundArrayTraits(String name)
    {
        super(name);
    }

    @Override
    public boolean requiresElementFactory()
    {
        return true;
    }
}
