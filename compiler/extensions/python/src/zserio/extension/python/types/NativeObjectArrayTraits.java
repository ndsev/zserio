package zserio.extension.python.types;

/**
 * Native Python array traits mapping for objects.
 */
public class NativeObjectArrayTraits extends NativeArrayTraits
{
    public NativeObjectArrayTraits()
    {
        super("ObjectArrayTraits", false, true);
    }
}