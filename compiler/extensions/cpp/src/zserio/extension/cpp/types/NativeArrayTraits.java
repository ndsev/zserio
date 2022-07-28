package zserio.extension.cpp.types;

import zserio.ast.PackageName;

/**
 * Native C++ array traits mapping.
 */
public class NativeArrayTraits extends NativeRuntimeType
{
    public enum TYPE
    {
        TEMPLATED,
        REQUIRES_ELEMENT_FIXED_BIT_SIZE,
        REQUIRES_ELEMENT_DYNAMIC_BIT_SIZE,
        REQUIRES_ELEMENT_FACTORY,
        NORMAL
    }

    public NativeArrayTraits(String name)
    {
        this(name, TYPE.NORMAL);
    }

    public NativeArrayTraits(String name, TYPE arrayTraitsType)
    {
        super(name, "zserio/ArrayTraits.h");

        switch (arrayTraitsType)
        {
        case TEMPLATED:
            isTemplated = true;
            requiresElementFixedBitSize = false;
            requiresElementDynamicBitSize = false;
            requiresElementFactory = false;
            break;

        case REQUIRES_ELEMENT_FIXED_BIT_SIZE:
            isTemplated = true;
            requiresElementFixedBitSize = true;
            requiresElementDynamicBitSize = false;
            requiresElementFactory = false;
            break;

        case REQUIRES_ELEMENT_DYNAMIC_BIT_SIZE:
            isTemplated = true;
            requiresElementFixedBitSize = false;
            requiresElementDynamicBitSize = true;
            requiresElementFactory = false;
            break;

        case REQUIRES_ELEMENT_FACTORY:
            isTemplated = true;
            requiresElementFixedBitSize = false;
            requiresElementDynamicBitSize = false;
            requiresElementFactory = true;
            break;

        case NORMAL:
        default:
            isTemplated = false;
            requiresElementFixedBitSize = false;
            requiresElementDynamicBitSize = false;
            requiresElementFactory = false;
            break;
        }
    }

    public NativeArrayTraits(PackageName packageName, String name, String systemIncludeFile)
    {
        super(packageName, name, systemIncludeFile);

        this.isTemplated = false;
        this.requiresElementFixedBitSize = false;
        this.requiresElementDynamicBitSize = false;
        this.requiresElementFactory = false;
    }

    public boolean isTemplated()
    {
        return isTemplated;
    }

    public boolean requiresElementFixedBitSize()
    {
        return requiresElementFixedBitSize;
    }

    public boolean requiresElementDynamicBitSize()
    {
        return requiresElementDynamicBitSize;
    }

    public boolean requiresElementFactory()
    {
        return requiresElementFactory;
    }

    private final boolean isTemplated;
    private final boolean requiresElementFixedBitSize;
    private final boolean requiresElementDynamicBitSize;
    private final boolean requiresElementFactory;
};
