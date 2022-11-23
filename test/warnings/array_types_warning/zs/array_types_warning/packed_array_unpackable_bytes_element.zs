package array_types_warning.packed_array_unpackable_bytes_element;

struct PackedArrayUnpackableBytesElement
{
    packed uint32 array1[];
    packed bytes array2[]; // unpackable
};
