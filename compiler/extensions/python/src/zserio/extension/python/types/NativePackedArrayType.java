package zserio.extension.python.types;

/**
 * Native Python packed array type mapping.
 */
public class NativePackedArrayType extends NativeArrayType
{
    public NativePackedArrayType(String packedArrayTraitsName, NativeArrayType arrayType)
    {
        super("packed_array", "PackedArray", arrayType.getArrayTraits(), packedArrayTraitsName);
    }
}