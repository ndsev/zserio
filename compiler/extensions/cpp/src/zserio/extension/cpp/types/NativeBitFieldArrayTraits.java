package zserio.extension.cpp.types;

/**
 * Native C++ bit field (fixed and dynamic with constant bit size) array traits mapping.
 */
public class NativeBitFieldArrayTraits extends NativeArrayTraits
{
    public NativeBitFieldArrayTraits()
    {
        super("BitFieldArrayTraits", NativeArrayTraits.TYPE.REQUIRES_ELEMENT_FIXED_BIT_SIZE);
    }
};
