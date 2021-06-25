package array_types_warning.packed_array_unpackable_string_element;

struct PackedArrayUnpackableStringElement
{
    packed uint32 array1[];
    packed string array2[]; // unpackable
};
