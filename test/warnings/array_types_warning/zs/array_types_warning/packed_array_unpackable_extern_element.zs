package array_types_warning.packed_array_unpackable_extern_element;

struct PackedArrayUnpackableExternElement
{
    packed uint32 array1[];
    packed extern array2[]; // unpackable
};
