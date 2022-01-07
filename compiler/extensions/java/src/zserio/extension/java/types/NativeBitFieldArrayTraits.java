package zserio.extension.java.types;

/**
 * Native Java bit field array traits mapping.
 */
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
