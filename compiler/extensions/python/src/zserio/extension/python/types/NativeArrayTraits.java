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

    public NativeArrayTraits(String name, boolean requiresElementBitSize, boolean requiresElementFactory)
    {
        this.name = name;
        this.requiresElementBitSize = requiresElementBitSize;
        this.requiresElementFactory = requiresElementFactory;
    }

    public String getName()
    {
        return name;
    }

    public boolean getRequiresElementBitSize()
    {
        return requiresElementBitSize;
    }

    public boolean getRequiresElementFactory()
    {
        return requiresElementFactory;
    }

    private final String name;
    private final boolean requiresElementBitSize;
    private final boolean requiresElementFactory;
};
