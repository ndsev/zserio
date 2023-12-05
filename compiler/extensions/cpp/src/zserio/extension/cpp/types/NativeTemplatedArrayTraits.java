package zserio.extension.cpp.types;

/**
 * Native C++ templated array traits mapping.
 */
public final class NativeTemplatedArrayTraits extends NativeArrayTraits
{
    public NativeTemplatedArrayTraits(String name)
    {
        super(name, NativeArrayTraits.TYPE.TEMPLATED);
    }
};
