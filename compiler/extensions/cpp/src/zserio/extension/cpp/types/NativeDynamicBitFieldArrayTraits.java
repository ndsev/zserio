package zserio.extension.cpp.types;

/**
 * Native C++ dynamic bit field (with dynamic bit size) array traits mapping.
 */
public final class NativeDynamicBitFieldArrayTraits extends NativeArrayTraits
{
    public NativeDynamicBitFieldArrayTraits()
    {
        super("DynamicBitFieldArrayTraits", NativeArrayTraits.TYPE.REQUIRES_ELEMENT_DYNAMIC_BIT_SIZE);
    }
};
