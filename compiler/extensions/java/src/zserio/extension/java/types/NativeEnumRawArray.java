package zserio.extension.java.types;

/**
 * Native Java object raw array mapping.
 */
public class NativeEnumRawArray extends NativeRawArray
{
    public NativeEnumRawArray()
    {
        super("EnumRawArray<>");
    }

    public boolean requiresElementClass()
    {
        return true;
    }
}