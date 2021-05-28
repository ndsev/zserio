package zserio.extension.python.types;

/**
 * Native Python packed array type mapping.
 */
public class NativePackedArrayType extends NativeArrayType
{
    public NativePackedArrayType(NativeArrayTraits arrayTraits, String packedArrayTraitsName)
    {
        super("packed_array", "PackedArray", arrayTraits, packedArrayTraitsName);
    }
}