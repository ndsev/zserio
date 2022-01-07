package zserio.extension.cpp.types;

/**
 * Native C++ bit field array traits mapping.
 */
public class NativeBitFieldArrayTraits extends NativeArrayTraits
{
    public NativeBitFieldArrayTraits()
    {
        super("BitFieldArrayTraits", true, true, false);
    }
};
