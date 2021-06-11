package packed_array_unpackable_extern_element_error;

struct PackedArrayUnpackableExternElementError
{
    packed uint32 array1[];
    packed extern array2[]; // unpackable
};
