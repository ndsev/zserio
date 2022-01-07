package zserio.extension.java.types;

/**
 * Native Java object raw array mapping.
 */
public class NativeObjectRawArray extends NativeRawArray
{
    public NativeObjectRawArray()
    {
        super("ObjectRawArray<>");
    }

    public boolean requiresElementClass()
    {
        return true;
    }
}