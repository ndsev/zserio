package zserio.extension.python.types;

/**
 * Native Python array traits mapping.
 */
public class NativeArrayTraits
{
    public NativeArrayTraits(String name)
    {
        this(name, false, false);
    }

    public NativeArrayTraits(String name, boolean requiresElementBitSize, boolean requiresElementCreator)
    {
        this.name = name;
        this.requiresElementBitSize = requiresElementBitSize;
        this.requiresElementCreator = requiresElementCreator;
    }

    public String getName()
    {
        return name;
    }

    public boolean getRequiresElementBitSize()
    {
        return requiresElementBitSize;
    }

    public boolean getRequiresElementCreator()
    {
        return requiresElementCreator;
    }

    private final String name;
    private final boolean requiresElementBitSize;
    private final boolean requiresElementCreator;
};
