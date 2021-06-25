package array_types_warning.packed_array_unpackable_float_element;

struct PackedArrayUnpackableFloatElement
{
    packed uint32 array1[];
    packed float64 array2[]; // unpackable
};
