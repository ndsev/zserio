package packed_array_unpackable_float_element_error;

struct PackedArrayUnpackableFloatElementError
{
    packed uint32 array1[];
    packed float64 array2[]; // unpackable
};
