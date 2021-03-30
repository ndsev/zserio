package zserio.extension.python.types;

/**
 * Native Python array type mapping for Zserio objects.
 */
public class NativeObjectArrayType extends NativeArrayType
{
    public NativeObjectArrayType(String traitsName)
    {
        super(traitsName, false, true);
    }
}
